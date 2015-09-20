package com.example.lenovo.livingwhere.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * 可用于传输的图片实体类
 */
public class FormImage {
    //��������
    private String mName;
    //�ļ���
    private String mFileName;
    //�ļ��� mime����Ҫ����ĵ���ѯ
    private String mMime;

    private Bitmap mBitmap;

    public FormImage(Bitmap mBitmap, String mName, String mFileName, String mMime) {
        this.mBitmap = mBitmap;
        this.mName = mName;
        this.mFileName = mFileName;
        this.mMime = mMime;
    }

    public String getName() {
        return mName;

    }

    public String getFileName() {
        return mFileName;
    }

    //��ͼƬ���ж�����ת��
    public byte[] getValue() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    public String getMime() {
        return mMime;
    }
}