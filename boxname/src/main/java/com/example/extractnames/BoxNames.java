package com.example.extractnames;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;

import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFreeText;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationHighlight;

/**
 * Hello world!
 *
 */
public class BoxNames {

  public static void main(String[] args) throws IOException {

    String formTemplate = "../forms/42101s.pdf";

    try (PDDocument pdfDocument = Loader.loadPDF(new File(formTemplate))) {
      // get the document catalog
      PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

      // as there might not be an AcroForm entry a null check is necessary
      if (acroForm != null) {

        Iterator<PDField> fields = acroForm.getFieldTree().iterator();
        while (fields.hasNext()) {

          PDField field = fields.next();
          List<PDAnnotationWidget> widgets = field.getWidgets();

          for (PDAnnotationWidget widget : widgets) {

            // Parse info from field
            PDPage page = widget.getPage();
            PDRectangle rectangle = widget.getRectangle();
            float left = rectangle.getLowerLeftX();
            float bottom = rectangle.getLowerLeftY();
            float right = rectangle.getUpperRightX();
            float top = rectangle.getUpperRightY();
            float width = rectangle.getWidth();
            float height = rectangle.getHeight();
            float[] quads = { left, top, right, top, left, bottom, right, bottom };

            // Use info to instantiate approriate highlight box and add to page
            PDAnnotationHighlight idAnnotation = new PDAnnotationHighlight();
            PDRectangle annotBox = new PDRectangle(left, bottom, width, height);
            idAnnotation.setColor(new PDColor(new float[] { 0, 1, 1 }, PDDeviceRGB.INSTANCE));
            idAnnotation.setConstantOpacity((float) 0.3);

            idAnnotation.setRectangle(annotBox);
            idAnnotation.setQuadPoints(quads);
            idAnnotation.setContents(field.getFullyQualifiedName());
            page.getAnnotations().add(idAnnotation);
          }
        }
      }
      pdfDocument.setAllSecurityToBeRemoved(true);
      pdfDocument.save("../forms/highlight_test.pdf");
    }
  }
  /**
   * public static void main(String[] args) throws IOException { String
   * formTemplate = "../forms/42101s.pdf";
   * 
   * try (PDDocument pdfDocument = Loader.loadPDF(new File(formTemplate))) { //
   * get the document catalog PDAcroForm acroForm =
   * pdfDocument.getDocumentCatalog().getAcroForm();
   * 
   * // as there might not be an AcroForm entry a null check is necessary if
   * (acroForm != null) { Map<String, float[]> boxLocations =
   * extractBoxSections(acroForm);
   * 
   * PDFTextStripperByArea stripper = initStripper(boxLocations);
   * 
   * if (stripper != null) {
   * 
   * List<String> names = stripper.getRegions();
   * 
   * int pg = 1; for (PDPage page : pdfDocument.getPages()) {
   * System.out.println("Printing page number " + pg);
   * stripper.extractRegions(page); for (String name : names) {
   * System.out.println(" Printing region " + name); System.out.println(" " +
   * stripper.getTextForRegion(name)); } pg++; } } else {
   * System.out.println("Stripper not properly initialized."); }
   * 
   * } else { System.out.println("No acroForm present."); }
   * 
   * // Save and close the filled out form.
   * pdfDocument.setAllSecurityToBeRemoved(true);
   * pdfDocument.save("../forms/test_box_names.pdf"); } }
   * 
   * public static boolean containsBox(PDRectangle container, PDRectangle box) {
   * 
   * boolean bounded_right = box.getUpperRightX() < container.getUpperRightX();
   * boolean bounded_top = box.getUpperRightY() < container.getUpperRightY();
   * boolean bounded_left = box.getLowerLeftX() > container.getLowerLeftX();
   * boolean bounded_bottom = box.getLowerLeftY() > container.getLowerLeftY();
   * 
   * if (bounded_right && bounded_left && bounded_top && bounded_bottom) { return
   * true; } else { return false; }
   * 
   * }
   * 
   * public static Map<String, float[]> extractBoxSections(PDAcroForm acroForm) {
   * 
   * Map<String, float[]> boxLocations = new HashMap<>(); Iterator<PDField> fields
   * = acroForm.getFieldIterator(); while (fields.hasNext()) {
   * 
   * PDField field = fields.next(); List<PDAnnotationWidget> widgets =
   * field.getWidgets();
   * 
   * int count = 1; for (PDAnnotationWidget widget : widgets) { PDRectangle
   * fieldBox = widget.getRectangle(); if (fieldBox != null) { float[] coord = {
   * fieldBox.getLowerLeftX(), fieldBox.getLowerLeftY(), fieldBox.getWidth(),
   * fieldBox.getHeight() }; String fieldName = field.getFullyQualifiedName() +
   * "_" + count; boxLocations.put(fieldName, coord); } } } return boxLocations; }
   * 
   * public static PDFTextStripperByArea initStripper(Map<String, float[]>
   * boxLocations) {
   * 
   * float A4X = PDRectangle.A4.getUpperRightX(); try {
   * 
   * PDFTextStripperByArea stripper = new PDFTextStripperByArea();
   * 
   * for (Map.Entry<String, float[]> boxLocation : boxLocations.entrySet()) {
   * String name = boxLocation.getKey(); float x = boxLocation.getValue()[0];
   * float y = boxLocation.getValue()[1]; float w = A4X - x; float h =
   * boxLocation.getValue()[3]; y = y + h;
   * 
   * stripper.addRegion(boxLocation.getKey(), new Rectangle2D.Float(x, y, w, h));
   * } return stripper;
   * 
   * } catch (IOException e) { e.printStackTrace(); return null; } }
   */
}
