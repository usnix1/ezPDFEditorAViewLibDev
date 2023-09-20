package udk.android.editor.test;

import android.os.Bundle;
import android.view.View;

import udk.android.editor.pdf.PackagedPDFDocumentRequestBuilder;
import udk.android.editor.view.pdf.PDFView;
import udk.android.util.LogUtil;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestPackageViewer extends TestBase{

	@Override
	public Runnable getAdditionalConfiguration(){
		return null;
	}//method

	@Override
	public Workable< View > getOnTest(){
		return null;
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		
		PDFView pdfView = getPDFView();
		
		try{
			
			PackagedPDFDocumentRequestBuilder reqBuilder = new PackagedPDFDocumentRequestBuilder();
			reqBuilder.add( "/sdcard/h.pdf", 1, 1 );
			reqBuilder.add( "/sdcard/h.pdf", 1, 1 );
			reqBuilder.add( "/sdcard/h.pdf", 1, 1 );
			String reqXml = reqBuilder.build();
			
			LogUtil.d( reqXml );
			
			pdfView.openPackagedPDFDocument( reqXml, 1 );
			
		}catch( Exception ex ){
			LogUtil.e( ex );
		}//try
	}//method

}//method
