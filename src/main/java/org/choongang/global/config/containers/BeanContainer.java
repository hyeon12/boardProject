package org.choongang.global.config.containers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.choongang.global.config.annotations.*;
import org.choongang.global.config.containers.mybatis.MapperProvider;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanContainer {
    private static BeanContainer instance;

    private Map<String, Object> beans; //클래스이름을 키로 하여, 생성된 객체를 저장

    private MapperProvider mapperProvider; // 마이바티스 매퍼 인스턴스

    public BeanContainer() {
        beans = new HashMap<>();
        mapperProvider = MapperProvider.getInstance();
    }

    public void loadBeans() {
        // 패키지 내의 클래스 스캔하여, 특정 애노테이션을 가진 클래스의 인스턴스를 생성 -> beans 맵에 저장
        try {
            //roothPath와 packageName을 이용해 프로젝트의 루트 경로 계산
            String rootPath = new File(getClass().getResource("../../../").getPath()).getCanonicalPath();
            String packageName = getClass().getPackageName().replace(".global.config.containers", "");
            List<Class> classNames = getClassNames(rootPath, packageName);

            for (Class clazz : classNames) {
                // 인터페이스는 동적 객체 생성을 하지 않으므로 건너뛰기
                if (clazz.isInterface()) {
                    continue;
                }

                // 애노테이션 중 Controller, RestController, Component, Service, ControllerAdvice, RestControllerAdvice 등이 TYPE 애노테이션으로 정의된 경우 beans 컨테이너에 객체 생성하여 보관
                // 키값은 전체 클래스명, 값은 생성된 객체
                String key = clazz.getName();

                /**
                 *  이미 생성된 객체라면 생성된 객체로 활용
                 *  매 요청시마다 새로 만들어야 객체가 있는 경우 갱신 처리
                 *
                 *  매 요청시 새로 갱신해야 하는 객체
                 *      - HttpServletRequest
                 *      - HttpServletResponse
                 *      - HttpSession session
                 *      - Mybatis mapper 구현 객체
                 */

                //beans 맵에 해당 클래스의 인스턴스 생성되어 저장되어 있는지 체크
                if (beans.containsKey(key)) {
                    updateObject(beans.get(key));
                    //저장되어 있다면 get(key)로 객체 갱신
                    continue;
                    //갱신만 하고 새로 생성하지 x
                }


                Annotation[] annotations = clazz.getDeclaredAnnotations();

                boolean isBean = false;
                for (Annotation anno : annotations) {
                    if (anno instanceof Controller || anno instanceof RestController || anno instanceof Service || anno instanceof Component || anno instanceof ControllerAdvice || anno instanceof RestControllerAdvice)  {
                        isBean = true;
                        break;
                    }
                }
                // 컨테이너가 관리할 객체라면 생성자 매개변수의 의존성을 체크하고 의존성이 있다면 해당 객체를 생성하고 의존성을 해결한다.
                if (isBean) {
                    Constructor con = clazz.getDeclaredConstructors()[0];
                    List<Object> objs = resolveDependencies(key, con);
                    if (!beans.containsKey(key)) {
                        Object obj = con.getParameterTypes().length == 0 ? con.newInstance() : con.newInstance(objs.toArray());
                        beans.put(key, obj);
                    }
                }

            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BeanContainer getInstance() {
        if (instance == null) {
            instance = new BeanContainer();
        }

        return instance;
    }

    /**
     * 생성된 객체 조회
     *
     * @param clazz
     * @return
     */
    public <T> T getBean(Class clazz) {
        return (T)beans.get(clazz.getName());
    }

    public void addBean(Object obj) {

        beans.put(obj.getClass().getName(), obj);
    }

    //외부에서 키,객체를 인수로 받아 수동으로 객체를 추가할 수 있는 인터페이스 생성
    public void addBean(String key, Object obj) {
        beans.put(key, obj);
    }

    // 전체 컨테이너 객체 반환
    public Map<String, Object> getBeans() {
        return beans;
    }

    /**
     * 의존성의 의존성을 재귀적으로 체크하여 필요한 의존성의 객체를 모두 생성한다.
     *
     * @param con
     */
    private List<Object> resolveDependencies(String key, Constructor con) throws Exception {
        //생성자의 매개변수를 재귀적으로 분석하여 필요한 의존성을 해결 + 필요한 객체 생성
        List<Object> dependencies = new ArrayList<>();
        if (beans.containsKey(key)) {
            //이미 생성된 객체 확인 -> beans 맵에 key가 존재하는지 확인하는 것
            dependencies.add(beans.get(key));
            return dependencies;
            //의존성 리스트 반환하고 메서드 종료
        }

        Class[] parameters = con.getParameterTypes();
        //필요한 의존성 생성
        if (parameters.length == 0) {
            Object obj = con.newInstance();
            dependencies.add(obj);
        } else {
            for(Class clazz : parameters) {
                /**
                 * 인터페이스라면 마이바티스 매퍼일수 있으므로 매퍼로 조회가 되는지 체크합니다.
                 * 매퍼로 생성이 된다면 의존성 주입이 될 수 있도록 dependencies에 추가
                 *
                  */
                if (clazz.isInterface()) {
                    Object mapper = mapperProvider.getMapper(clazz);
                    if (mapper != null) {
                        dependencies.add(mapper);
                        continue;
                    }
                }

                Object obj = beans.get(clazz.getName());
                if (obj == null) {
                    Constructor _con = clazz.getDeclaredConstructors()[0];

                    if (_con.getParameterTypes().length == 0) {
                        obj = _con.newInstance();
                    } else {
                        List<Object> deps = resolveDependencies(clazz.getName(), _con);
                        obj = _con.newInstance(deps.toArray());
                    }
                }
                dependencies.add(obj);
            }
        }


        return dependencies;
    }

    private List<Class> getClassNames(String rootPath, String packageName) {
        //지정된 패키지 내의 모든 클래스 반환
        List<Class> classes = new ArrayList<>();
        List<File> files = getFiles(rootPath);//루트 내의 모든 파일,디렉토리 검색
        for (File file : files) {
            String path = file.getAbsolutePath();
            //파일 경로를 클래스 이름으로 변환
            String className = packageName + "." + path.replace(rootPath + File.separator, "").replace(".class", "").replace(File.separator, ".");
            try {
                //class.forName -> 클래스 로드 + 리스트에 추가
                Class cls = Class.forName(className);
                classes.add(cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    private List<File> getFiles(String rootPath) {
        //지정된 경로 내의 모든 파일, 디렉토리를 재귀적으로 검색
        List<File> items = new ArrayList<>();
        File[] files = new File(rootPath).listFiles();
        if (files == null) return items;

        for (File file : files) {
            if (file.isDirectory()) {
                List<File> _files = getFiles(file.getAbsolutePath());
                if (!_files.isEmpty()) items.addAll(_files);
            } else {
                items.add(file);
            }
        }
        return items;
    }

    /**
     * 컨테이너에 이미 담겨 있는 객체에서 매 요청시마다 새로 생성이 필요한 의존성이 있는 경우
     * 갱신 처리
     *  - HttpServletRequest
     *  - HttpServletResponse
     *  - HttpSession session
     *  - Mybatis mapper 구현 객체
     *
     * @param bean
     */
    //서블릿 객체와 마이바티스 매퍼 객체를 갱신
    private void updateObject(Object bean) {
        // 인터페이스인 경우 갱신 배제
        if (bean.getClass().isInterface()) {
            return;
        }

        Class clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class clz = field.getType();
            try {

                /**
                 * 필드가 마이바티스 매퍼 또는 서블릿 기본 객체(HttpServletRequest, HttpServletResponse, HttpSession) 이라면 갱신
                 *
                 */
                
                Object mapper = mapperProvider.getMapper(clz);
                //mybatis 매퍼 객체 조회

                // 서블릿 기본 객체(HttpServletRequest, HttpServletResponse, HttpSession)이라면 갱신
                if (clz == HttpServletRequest.class || clz == HttpServletResponse.class || clz == HttpSession.class || mapper != null) {
                    field.setAccessible(true);
                }

                if (clz == HttpServletRequest.class) {
                    field.set(bean, getBean(HttpServletRequest.class));
                } else if (clz == HttpServletResponse.class) {
                    field.set(bean, getBean(HttpServletResponse.class));
                } else if (clz == HttpSession.class) {
                    field.set(bean, getBean(HttpSession.class));
                } else if (mapper != null) { // 마이바티스 매퍼
                    field.set(bean, mapper);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
