package com.example.wordwizard.common

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import java.io.File
import java.io.FileOutputStream


class ConvertTextToPdf(val text: String, fileName: String) {
    private val pdfWriter = PdfWriter(FileOutputStream(fileName))
    private val pdfDocument = PdfDocument(pdfWriter)
    private val document = Document(pdfDocument)

    fun savePdfToStorage(pdfFilePath: String, outputFilePath: String) {

        /**Вставка текста в документ**/
        document.add(Paragraph(text).setTextAlignment(TextAlignment.LEFT))
        document.close()

        /**Копирование в другой файл, расположенный в другой папке**/
        val pdfFile = File(pdfFilePath)
        val outputFile = File(outputFilePath)

        pdfFile.copyTo(outputFile, true)
    }
}