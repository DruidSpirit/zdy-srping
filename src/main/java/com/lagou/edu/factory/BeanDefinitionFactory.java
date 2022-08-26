package com.lagou.edu.factory;

import com.lagou.edu.annotation.*;
import com.lagou.edu.dto.BeanDefinition;
import com.lagou.edu.enums.ProxyType;
import com.lagou.edu.utils.UtilForData;
import com.mysql.cj.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.reflections.Reflections;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class BeanDefinitionFactory {

    /**
     * 注解配置
     */
    private Element annotationAllow;
    /**
     * beans配置
     */
    private Element beansElement;

    /**
     * 代理模式
     */
    private ProxyType proxyType = ProxyType.JDK_PROXY;

    /**
     * bean定义集合实例（这里为了控制注入顺序用到TreeMap)
     */
    private Map<String,BeanDefinition> beanDefinitions = new TreeMap<>();

    public BeanDefinitionFactory( String xmlPath ) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(xmlPath);
        pares( resourceAsStream );
    }

    /**
     * 获取bean定义集合
     * @return        bean定义集合实例
     */
    public Map<String,BeanDefinition>  getBeanDefinitionList(){

        getXmlBeanDefinitions();
        getAnnotationBeanDefinitions();

        return beanDefinitions;
    }

    /**
     * 解析xml配置文件
     */
    private void pares( InputStream resourceAsStream ){

        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            this.annotationAllow = rootElement.element("annotation-allow");
            this.beansElement = rootElement.element("beans");
            String type = rootElement.element("proxy").attributeValue("type");
            ProxyType enumByCode = UtilForData.getEnumByCode(ProxyType.values(), ProxyType::getType, type);
            if ( enumByCode != null ){
                this.proxyType = enumByCode;
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取xml形式配置的bean集合
     */
    private void getXmlBeanDefinitions(){

        List<Element> beanList = beansElement.selectNodes("//bean");

        //  循环处理bean实例
        for (int i = 0; i < beanList.size(); i++) {
            Element element =  beanList.get(i);
            // 处理每个bean元素，获取到该元素的id 和 class 属性
            String id = element.attributeValue("id");        // accountDao
            String clazz = element.attributeValue("class");  // com.lagou.edu.dao.impl.JdbcAccountDaoImpl
            // 通过反射技术实例化对象
            Class<?> aClass = null;
            try {
                aClass = Class.forName(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            // 存储到map中待用
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanId(id);
            beanDefinition.setBeanClass( aClass );
            beanDefinitions.put(id,beanDefinition);

        }

        // 实例化完成之后维护对象的依赖关系，检查哪些对象需要传值进入，根据它的配置，我们传入相应的值
        // 有property子元素的bean就有传值需求
        List<Element> propertyList = beansElement.selectNodes("//property");
        // 解析property，获取父元素
        for (int i = 0; i < propertyList.size(); i++) {
            Element element =  propertyList.get(i);   //<property name="AccountDao" ref="accountDao"></property>
            String name = element.attributeValue("name");
            String ref = element.attributeValue("ref");

            // 找到当前需要被处理依赖关系的bean
            Element parent = element.getParent();

            // 调用父元素对象的反射功能
            String parentId = parent.attributeValue("id");
            BeanDefinition beanDefinition = beanDefinitions.get(parentId);
            // 遍历父对象中的所有方法，找到"set" + name
            Method[] methods = beanDefinition.getBeanClass().getMethods();
            for (int j = 0; j < methods.length; j++) {
                Method method = methods[j];
                if(method.getName().equalsIgnoreCase("set" + name)) {  // 该方法就是 setAccountDao(AccountDao accountDao)
                    //method.invoke(parentObject,map.get(ref));
                    beanDefinition.propertyPut( method.getName(),ref );
                }
            }

        }
    }


    /**
     * 获取注解形式获取的bean集合
     */
    private void getAnnotationBeanDefinitions(){

        //  解析并判断注解是否开启
        String scanPath = annotationAllow.attributeValue("scanPath");

        Class[] scanAnnotated = {Repository.class,Service.class, Component.class};
        //  扫描注解包文件
        if ( scanPath != null ) {
            Reflections reflections = new Reflections(scanPath);
            //  定义获取扫描注解

            //  合并匹配到注解的反射类
            for (Class aClass : scanAnnotated) {
                Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(aClass);
                annotatedWith.forEach(o -> setBeanDefinitionInfoByAnnotationInfo(o));
            }

        }

    }

    /**
     * 获取class反射类上的注解并将相应的信息封装到BeanDefinitionI中
     */
    private void setBeanDefinitionInfoByAnnotationInfo( Class<?> clazz ){

        //  获取类注解解析
        String classAnnotationValue = null;
        Component component = clazz.getAnnotation(Component.class);
        Service service = clazz.getAnnotation(Service.class);
        Repository repository = clazz.getAnnotation(Repository.class);

        if ( component != null ) {
            classAnnotationValue = component.value();
        }else if ( service != null ){
            classAnnotationValue = service.value();
        }else if ( repository != null ){
            classAnnotationValue = repository.value();
        }
        //  如果注解值没加则默认以类名作为beanId
        if ( StringUtils.isNullOrEmpty(classAnnotationValue) ){
            classAnnotationValue = clazz.getName();
        }
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanId(classAnnotationValue);
        beanDefinition.setBeanClass( clazz );

        //  获取属性注解解析
        Field[] declaredFields = clazz.getDeclaredFields();
        for ( Field field: declaredFields ) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if ( autowired != null && !StringUtils.isNullOrEmpty(autowired.value()) ){
                //  首字母大写
                String fieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                String autowiredMethodName = "set" + fieldName;
                beanDefinition.propertyPut( autowiredMethodName, autowired.value() );
            }
        }
        beanDefinitions.put(classAnnotationValue,beanDefinition);


    }

    public Element getAnnotationAllow() {
        return annotationAllow;
    }

    public void setAnnotationAllow(Element annotationAllow) {
        this.annotationAllow = annotationAllow;
    }

    public Element getBeansElement() {
        return beansElement;
    }

    public void setBeansElement(Element beansElement) {
        this.beansElement = beansElement;
    }

    public ProxyType getProxyType() {
        return proxyType;
    }

    public void setProxyType(ProxyType proxyType) {
        this.proxyType = proxyType;
    }
}
