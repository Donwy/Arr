package com.gzch.lsplat.work.db;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

/**
 * @author LY
 * 加密收据库
 */
public class EncryptionSql {

    private static EncryptionSql dBencrypt;
    private Boolean isOpen = true;

    public static EncryptionSql getInstences() {
        if (dBencrypt == null) {
            synchronized (EncryptionSql.class) {
                if (dBencrypt == null) {
                    dBencrypt = new EncryptionSql();
                }
            }
        }
        return dBencrypt;
    }

    /**
     * 如果有旧表 先加密数据库
     *
     * @param context
     * @param passphrase
     */
    public void encrypt(Context context, String dbName, String passphrase) {
        File file = context.getDatabasePath(dbName);
        if (file.exists()) {
            if (isOpen) {
                try {
                    File newFile = File.createTempFile("sqlcipherutils", "tmp", context.getCacheDir());
                    net.sqlcipher.database.SQLiteDatabase db = net.sqlcipher.database.SQLiteDatabase.openDatabase(
                            file.getAbsolutePath(), "", null, SQLiteDatabase.OPEN_READWRITE);

                    db.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s';",
                            newFile.getAbsolutePath(), passphrase));
                    db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
                    db.rawExecSQL("DETACH DATABASE encrypted;");

                    int version = db.getVersion();
                    db.close();

                    db = net.sqlcipher.database.SQLiteDatabase.openDatabase(newFile.getAbsolutePath(),
                            passphrase, null,
                            SQLiteDatabase.OPEN_READWRITE);

                    db.setVersion(version);
                    db.close();
                    file.delete();
                    newFile.renameTo(file);
                    isOpen = false;
                } catch (Exception e) {
                    isOpen = false;
                }
            }
        }
    }


//    public static void encrypt(Context ctxt, String dbName, String passphrase){
//        File originalFile = ctxt.getDatabasePath(dbName);
//        System.out.println("EncryptionSql.encrypt == 1111");
//        if (originalFile.exists()) {
//            File newFile = null;
//            try {
//                newFile = File.createTempFile("sqlcipherutils", "tmp",
//                                ctxt.getCacheDir());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (newFile == null) {
//                SQLiteDatabase db = SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(),
//                        "", null,
//                        SQLiteDatabase.OPEN_READWRITE);
//
//                db.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s';",
//                        newFile.getAbsolutePath(), passphrase));
//                db.beginTransaction();
//                db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
//                db.endTransaction();
//                db.rawExecSQL("DETACH DATABASE encrypted;");
//
//                int version = db.getVersion();
//
//                db.close();
//
//                db = SQLiteDatabase.openDatabase(newFile.getAbsolutePath(),
//                        passphrase, null,
//                        SQLiteDatabase.OPEN_READWRITE);
//                db.setVersion(version);
//                db.close();
//
//                originalFile.delete();
//                newFile.renameTo(originalFile);
//            }
//        }
//        System.out.println("EncryptionSql.encrypt == 2222");
//    }

}
