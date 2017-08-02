package org.monarchinitiative.hpotextmining.application;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContextRule implements TestRule {

    private Class clazz;
    private Object target;


    public <T> SpringContextRule(Class<T> clazz, Object target) {
        this.clazz = clazz;
        this.target = target;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(clazz);
                context.getAutowireCapableBeanFactory().autowireBean(target);
                context.start();
                try {
                    base.evaluate();
                } finally {
                    context.close();
                }
            }
        };
    }
}
