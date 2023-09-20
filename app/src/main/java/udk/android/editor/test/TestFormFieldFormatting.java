package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.GlobalConfigurationService;
import udk.android.editor.view.pdf.PDFView;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestFormFieldFormatting extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.NEW_INPUT_METHOD = true;
				LibConfiguration.USE_FORM = true;
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
		
		getPDFView().openPDF( "/sdcard/sz016.pdf", 0 );
		
	}//method

}//method
