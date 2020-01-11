package club.sk1er.items.tweaker;

import club.sk1er.items.asm.EntityItemTransformer;
import club.sk1er.items.asm.ItemEnchantedBookTransformer;
import club.sk1er.items.tweaker.transform.ItemTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import club.sk1er.items.asm.ItemPotionTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

public class ClassTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger("ItemTransformer");
    private final Multimap<String, ItemTransformer> transformerMap = ArrayListMultimap.create();

    public ClassTransformer() {
        registerTransformer(new EntityItemTransformer());
        registerTransformer(new ItemPotionTransformer());
        registerTransformer(new ItemEnchantedBookTransformer());
    }

    private void registerTransformer(ItemTransformer transformer) {
        for (String cls : transformer.getClassNames()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) return null;

        Collection<ItemTransformer> transformers = transformerMap.get(transformedName);
        if (transformers.isEmpty()) return bytes;

        LOGGER.info("Found {} transformers for {}", transformers.size(), transformedName);

        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        transformers.forEach(transformer -> {
            LOGGER.info("Applying transformer {} on {}...", transformer.getClass().getName(), transformedName);
            transformer.transform(classNode, transformedName);
        });

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        try {
            classNode.accept(classWriter);
        } catch (Throwable e) {
            System.out.println("Exception when transforming " + transformedName + " : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }

        return classWriter.toByteArray();
    }
}
