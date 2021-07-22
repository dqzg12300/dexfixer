package com.android.dx.unpacker;

import com.android.dex.Dex;
import com.android.dex.util.FileUtils;
import com.android.dx.command.dexer.DxContext;
import com.android.dx.merge.CollisionPolicy;
import com.android.dx.merge.DexMerger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javafx.scene.shape.Path;

class DexFixer
{
    public static void main(String[] args) throws IOException
    {
        if (args.length < 3)
        {
            printUsage();
            return;
        }

        String dexpath=args[0];
        String binpath=args[1];
        String outpath=args[2];

        File dexfile = new File(dexpath);
        File binfile = new File(binpath);
        if(!dexfile.exists() || !binfile.exists()){
            System.out.println("Usage: dexfile or binfile not found");
            return;
        }
        if (!dexfile.getPath().endsWith(".dex")) {
            System.out.println("Usage: DexFixer dexpath end not .dex");
            return;
        }

        Dex[] dexes = new Dex[1];
        dexes[0] = new Dex(dexfile);
        MethodCodeItemFile methodCodeItemFile = new MethodCodeItemFile(binfile);
        Dex merged = new DexMerger(dexes, CollisionPolicy.KEEP_FIRST, new DxContext(),
                methodCodeItemFile.getMethodCodeItems()).merge();
        merged.writeTo(new File(outpath));
        System.out.println("success");
//        String unpackerPath = args[0];
//        File unpackerDir = new File(unpackerPath);
//        File dexDir = new File(unpackerPath + "/dex");
//        File methodDir = new File(unpackerPath + "/method");
//        if (!unpackerDir.isDirectory() || !dexDir.isDirectory() || !methodDir.isDirectory())
//        {
//            printUsage();
//        }
//
//        String outputPath = args[1];
//        File outputDir = new File(outputPath);
//        if (!outputDir.exists())
//        {
//            if (!outputDir.mkdir())
//            {
//                System.out.println("Error: mkdir " + outputDir + " fail!");
//                System.exit(-1);
//            }
//        }
//
//        File[] dexFiles = dexDir.listFiles();
//        assert dexFiles != null;
//        for (File dexFile : dexFiles)
//        {
//            if (!dexFile.getPath().endsWith(".dex")) {
//                continue;
//            }
//
//            Dex[] dexes = new Dex[1];
//            dexes[0] = new Dex(dexFile);
//            String methodCodeItemPath = methodDir + "/" + dexFile.getName().substring(0,
//                    dexFile.getName().length() - 4) + "_codeitem.bin";
//            String outputDexPath = outputPath + "/" + dexFile.getName();
//            File file = new File(methodCodeItemPath);
//            if (!file.exists())
//            {
//                System.out.println("Warn:" + methodCodeItemPath + " not exists!");
//                Files.copy(dexFile.toPath(), new File(outputDexPath).toPath(),
//                        StandardCopyOption.REPLACE_EXISTING);
//                continue;
//            }
//            MethodCodeItemFile methodCodeItemFile = new MethodCodeItemFile(file);
//            Dex merged = new DexMerger(dexes, CollisionPolicy.KEEP_FIRST, new DxContext(),
//                    methodCodeItemFile.getMethodCodeItems()).merge();
//            merged.writeTo(new File(outputDexPath));
//        }


    }

    private static void printUsage()
    {
        System.out.println("Usage: DexFixer unpacker output");
    }
}
