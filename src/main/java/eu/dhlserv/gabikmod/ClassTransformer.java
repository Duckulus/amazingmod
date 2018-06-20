package eu.dhlserv.gabikmod;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.Minecraft") || name.equals("ave")) {
            return transformMethods(basicClass, new BiConsumer<ClassNode, MethodNode>() {
                @Override
                public void accept(ClassNode clazz, MethodNode method) {
                    transformMinecraft(clazz, method);
                }
            });
        }
        return basicClass;
    }

    private byte[] transformMethods(byte[] bytes, final BiConsumer<ClassNode, MethodNode> transformer) {
        ClassReader classReader = new ClassReader(bytes);
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        classNode.methods.forEach(new Consumer<MethodNode>() {
            @Override
            public void accept(MethodNode m) {
                if (m.desc.equals("()V")) {
                    transformer.accept(classNode, m);
                }
            }
        });

        ClassWriter classWriter = new ClassWriter(3);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    int rootIndex = 0;

    private void transformMinecraft(ClassNode clazz, MethodNode method) {
        if (rootIndex == -1) {
            return;
        }
        Iterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = iter.next();

            if (insn instanceof LdcInsnNode) {
                if (((LdcInsnNode) insn).cst.equals("root")) {
                    System.out.println("Found root");
                    rootIndex++;
                    if (rootIndex == 2) {
                        System.out.println("Inserting method");
                        InsnList list = new InsnList();
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(AccumulationMethod.class), "createAccumulation",
                                Type.getMethodDescriptor(Type.getType(Void.TYPE)), false));
                        method.instructions.insert(insn, list);
                        rootIndex = -1;
                        break;
                    }
                }
            }
        }
    }

}
