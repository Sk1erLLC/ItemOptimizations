package club.sk1er.items.asm;

import club.sk1er.items.tweaker.transform.ItemTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ItemEnchantedBookTransformer implements ItemTransformer {
    @Override
    public String[] getClassNames() {
        return new String[]{"net.minecraft.item.ItemEnchantedBook"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("hasEffect") || methodName.equals("func_77636_d")) {
                methodNode.instructions.clear();
                methodNode.localVariables.clear();
                methodNode.instructions.add(returnFalse());
            }
        }
    }

    private InsnList returnFalse() {
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.IRETURN));
        return list;
    }
}
