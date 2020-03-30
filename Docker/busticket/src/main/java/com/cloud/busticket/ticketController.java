package com.cloud.busticket;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Paths;

@RestController
@RequestMapping("/download")
public class ticketController {
    @Autowired
    ServletContext context;

    @RequestMapping(value = "/pdf/Bus_ticket.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<InputStreamResource> download(@RequestBody Ticket ticket) throws IOException {
        System.out.println("Calling Download:-Bus_ticket.pdf");
        String book_date = ticket.getBook_date();
        String name = ticket.getName();
        String from = ticket.getFrom();
        String to = ticket.getTo();
        String dep_date = ticket.getDep_date();
        String nseats = ticket.getNseats();
        String total = ticket.getTotal();

        File pdfFile = Paths.get("pdf/Bus_ticket.pdf").toFile();
        InputStream is = new FileInputStream(pdfFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(is,null);
        try {
            PdfStamper stamper = new PdfStamper(reader,baos);
            AcroFields form = stamper.getAcroFields();
            form.setField("book_date",book_date);
            form.setField("name",name);
            form.setField("from",from);
            form.setField("to",to);
            form.setField("dep_date",dep_date);
            form.setField("nseats",nseats);
            form.setField("total",total);
            BarcodeEAN barcodeEAN = new BarcodeEAN();
            barcodeEAN.setCodeType(barcodeEAN.EAN13);
            long number = (long) Math.floor(Math.random() * 9000000000000L) + 1000000000000L;
            barcodeEAN.setCode(String.valueOf(number));
            PdfContentByte cb = stamper.getOverContent(reader.getNumberOfPages());
            System.out.println(cb);
            Image imageEAN = barcodeEAN.createImageWithBarcode(cb, null, null);
            PushbuttonField ad = form.getNewPushbuttonFromField("barcode_af_image");
            ad.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
            ad.setProportionalIcon(true);
            ad.setImage(imageEAN);
            form.replacePushbuttonField("barcode_af_image", ad.getField());
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        final byte[] bytes =baos.toByteArray();
        InputStream iso = new ByteArrayInputStream(bytes);
        return ResponseEntity.ok().headers(headers).contentLength(iso.available())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(new InputStreamResource(iso));

    }
}
