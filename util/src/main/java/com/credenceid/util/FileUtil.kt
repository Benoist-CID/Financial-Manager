package com.credenceid.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileUtil {
    fun write(file: File, line: String) {
        val fos = FileOutputStream(file)

        fos.write(line.toByteArray())
        fos.close()
    }

    fun read(file: File): String {
        val fis = FileInputStream(file)
        val bytesRead = fis.readBytes()

        fis.close()
        return String(bytesRead)
    }
 }