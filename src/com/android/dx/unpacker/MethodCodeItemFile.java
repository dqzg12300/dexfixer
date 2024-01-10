package com.android.dx.unpacker;

import com.android.dex.util.ByteInput;
import com.android.dex.util.ByteOutput;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MethodCodeItemFile
{
    private ByteBuffer data;
    private Map<Integer, MethodCodeItem> map;
    private String jsondata;

    public MethodCodeItemFile(File file)
    {
        try (InputStream inputStream = new FileInputStream(file))
        {
            loadFrom(inputStream);
            this.map = new HashMap<>();
            Gson gson=new Gson();
            String[] items=this.jsondata.split(";");
            if(items.length<=2){
                items=this.jsondata.split("\n");
            }
            for(int i=0;i<items.length;i++){
                String codeJson=items[i];
//                System.out.println("codeJson:"+codeJson);
                //发现保存的json有点问题，应该给base64的值用双引号包起来，不然gson会出现解析失败的
                codeJson=codeJson.replace("ins:","ins:\"");
                codeJson=codeJson.replace("}","\"}");
                codeJson=codeJson.replace("name:","name:\"");
                codeJson=codeJson.replace(",method_idx","\",method_idx");

                JsonCodeItem codedata= gson.fromJson(codeJson,JsonCodeItem.class);
                MethodCodeItem codeItem = new MethodCodeItem();
                codeItem.index = codedata.method_idx;
                codeItem.descriptor = codedata.name;
                codeItem.size = codedata.code_item_len;
                byte[] buff= Base64.getDecoder().decode(codedata.ins);
                codeItem.code = buff;
                this.map.put(codeItem.index, codeItem);
            }

//            while (data.hasRemaining())
//            {
//                MethodCodeItem codeItem = new MethodCodeItem();
//                codeItem.index = readInt();
//                codeItem.descriptor = readCString();
//                codeItem.size = readInt();
//                codeItem.code = readByteArray(codeItem.size);
//                this.map.put(codeItem.index, codeItem);
//            }
        }
        catch (Exception e)
        {
            System.out.println("Warn: " + file.getPath() + " maybe invalid format!");
            e.printStackTrace();
        }
    }

    public Map<Integer, MethodCodeItem> getMethodCodeItems()
    {
        return this.map;
    }

    public byte[] readByteArray(int length)
    {
        byte[] result = new byte[length];
        data.get(result);
        return result;
    }

    public int readInt()
    {
        return data.getInt();
    }

    public String readCString()
    {
        byte b;
        StringBuilder s = new StringBuilder("");
        do
        {
            b = data.get();
            if (b != 0)
            {
                s.append((char) b);
            }
        }
        while (b != 0);
        return String.valueOf(s);
    }

    private void loadFrom(InputStream in) throws IOException
    {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];

        int count;
        while ((count = in.read(buffer)) != -1)
        {
            bytesOut.write(buffer, 0, count);
        }

//        this.data = ByteBuffer.wrap(bytesOut.toByteArray());
//        this.data.order(ByteOrder.LITTLE_ENDIAN);
        this.jsondata=bytesOut.toString();
    }
}
