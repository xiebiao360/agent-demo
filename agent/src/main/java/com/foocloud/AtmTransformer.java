package com.foocloud;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class AtmTransformer implements ClassFileTransformer {
    private static Logger LOGGER = LoggerFactory.getLogger(AtmTransformer.class);

    private static final String WITHDRAW_MONEY_METHOD = "withdrawMoney";

    private final String targetClassName;
    private final ClassLoader targetClassLoader;

    public AtmTransformer(String className, ClassLoader classLoader) {
        this.targetClassName = className;
        this.targetClassLoader = classLoader;
    }

    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) {
        byte[] byteCode = classfileBuffer;
        String finalTargetClassName = this.targetClassName
                .replaceAll("\\.", "/");
        if (!className.equals(finalTargetClassName)) {
            return byteCode;
        }

        if (className.equals(finalTargetClassName)
                && loader.equals(targetClassLoader)) {

            System.out.println("[Agent] Transforming class MyAtm");
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(targetClassName);
                CtMethod m = cc.getDeclaredMethod(WITHDRAW_MONEY_METHOD);
                m.addLocalVariable(
                        "startTime", CtClass.longType);
                m.insertBefore(
                        "startTime = System.currentTimeMillis();");

                StringBuilder endBlock = new StringBuilder();

                m.addLocalVariable("endTime", CtClass.longType);
                m.addLocalVariable("opTime", CtClass.longType);
                endBlock.append(
                        "endTime = System.currentTimeMillis();");
                endBlock.append(
                        "opTime = (endTime-startTime)/1000;");

                endBlock.append(
                        "LOGGER.info(\"[Application] Withdrawal operation completed in:" +
                                "\" + opTime + \" seconds!\");");

                m.insertAfter(endBlock.toString());

                byteCode = cc.toBytecode();
                cc.detach();
            } catch (NotFoundException | CannotCompileException | IOException e) {
                LOGGER.error("Exception", e);
            }
        }
        return byteCode;
    }
}
