package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.GlobalConfigurationService;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestDigitalSignature extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.NEW_INPUT_METHOD = true;
				LibConfiguration.USE_FORM = true;
				LibConfiguration.USE_SIGNATURE_TOOLKIT = true;
				LibConfiguration.USE_SIGNATURE_NPKI = true;
				LibConfiguration.USE_SIGNATURE_INNER_HANDLING = true;
				GlobalConfigurationService.getInstance().setFieldFormattingWithKoreanStyle( true );
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		return null;
	}//method

	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		final PDFView pdfView = getPDFView();
		
		getPDFView().openPDF( "/sdcard/empty.sign.pdf", 0 );
//		getPDFView().openPDF( "/sdcard/2015081000000533.pdf", 0 );
	}//method

}//method
