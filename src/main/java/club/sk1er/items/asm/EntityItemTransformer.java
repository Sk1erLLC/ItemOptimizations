package club.sk1er.items.asm;

import club.sk1er.items.tweaker.transform.ItemTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EntityItemTransformer implements ItemTransformer {
    @Override
    public String[] getClassNames() {
        return new String[]{"net.minecraft.entity.item.EntityItem"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("searchForOtherItemsNearby") || methodName.equals("func_85054_d")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), stopSearch());
            }

            if (methodName.equals("combineItems") || methodName.equals("func_70289_a")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), stopSearchBoolean());
            }
        }
    }

    private InsnList stopSearch() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/entity/item/EntityItem", "func_92059_d",
                "()Lnet/minecraft/item/ItemStack;", false));
        list.add(new VarInsnNode(Opcodes.ASTORE, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", "field_77994_a", "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "func_77976_d", "()I", false));
        LabelNode ificmplt = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPLT, ificmplt));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ificmplt);
        return list;
    }

    private InsnList stopSearchBoolean() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/entity/item/EntityItem", "func_92059_d",
                "()Lnet/minecraft/item/ItemStack;", false));
        list.add(new VarInsnNode(Opcodes.ASTORE, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", "field_77994_a", "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "func_77976_d", "()I", false));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPLT, labelNode));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(labelNode);
        return list;
    }
}
