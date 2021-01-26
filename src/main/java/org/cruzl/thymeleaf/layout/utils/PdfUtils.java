package org.cruzl.thymeleaf.layout.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfUtils {

	private SpringTemplateEngine templateEngine;

	public PdfUtils(SpringTemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public byte[] createPdf(@NonNull String templateName, Map<String, Object> variables) {
		byte[] pdfRaw = null;
		Context context = new Context();
		if (variables != null) {
			for (Entry<String, Object> variable : variables.entrySet()) {
				context.setVariable(variable.getKey(), variable.getValue());
			}
		}

		String templateProcessed = templateEngine.process(templateName, context);
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ITextRenderer renderer = new ITextRenderer();
			if (variables.containsKey("fonts")) {
				for (String font : (List<String>) variables.get("fonts")) {
					renderer.getFontResolver().addFont(font, true);
				}
			}
			renderer.setDocumentFromString(templateProcessed,
					new ClassPathResource("/static/").getURL().toExternalForm());
			renderer.layout();
			renderer.createPDF(os, false);
			renderer.finishPDF();

			pdfRaw = os.toByteArray();
		} catch (DocumentException | IOException e) {
			log.error("Error creating PDF", e);
		}

		return pdfRaw;
	}
}
