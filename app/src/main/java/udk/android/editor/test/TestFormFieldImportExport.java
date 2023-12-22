package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.PDFView;
import udk.android.reader.view.pdf.PDFViewAdapter;
import udk.android.reader.view.pdf.PDFViewEvent;
import udk.android.util.FileUtil;
import udk.android.util.IOUtil;
import udk.android.util.LogUtil;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestFormFieldImportExport extends TestBase{

	private static boolean TEST_LEGACY = true;
	private static String TEST_FILENAME = "PC_216035023883.pdf";;
	
	private PDFView pdfView;
	private String filename;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.USE_FORM = true;
				LibConfiguration.LQ_PRERENDER = true;
				LibConfiguration.USE_EBOOK_MODE = false;
				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_VERTICAL;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
				
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		
		final Context context = this;
		
		return new Workable< View >(){
			@Override
			public void work( View tool ){

				String xml = pdfView.getFormValidationXML();
				try{
					IOUtil.writeStringToFile( getWorkspace().getAbsolutePath() + File.separator + filename + ".validation.xml", xml, "UTF-8" );
				}catch( Exception ex ){
					LogUtil.e( ex );
				}//try
				String exml = null;
				if( TEST_LEGACY ){
					exml = pdfView.exportFormDataXML( null );
				}else{
					StringWriter sw = new StringWriter();
					pdfView.exportUnidocsFormDataXML( sw, null );
					sw.flush();
					exml = sw.toString();
				}
				try{
					IOUtil.writeStringToFile( getWorkspace().getAbsolutePath() + File.separator + filename + ".export.xml", exml, "UTF-8" );
				}catch( Exception ex ){
					LogUtil.e( ex );
				}//try
				
			}//method
		};
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		pdfView = getPDFView();
		pdfView.setPDFViewListener( new PDFViewAdapter(){
			@Override
			public void onOpenCompleted( PDFViewEvent e ){
				try{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					pdfView.getAddData( baos );
					String i = new String( baos.toByteArray(), "UTF-8" );
					if( TEST_LEGACY ){
						pdfView.importFormDataXML( i, "UTF-8", true );
					}else{
						InputStream is = new ByteArrayInputStream( i.getBytes( "UTF-8" ) );
						pdfView.importUnidocsFormDataXML( is, true );
					}
					IOUtil.writeStringToFile( getWorkspace().getAbsolutePath() + File.separator + filename + ".import.xml", i, "UTF-8" );

				}catch( Exception ex ){
					LogUtil.e( ex );
				}//try
			}//mehod
		} );

		String path = getWorkspace().getAbsolutePath() + File.separator + TEST_FILENAME;
		String tempPath = path + ".copy.pdf";
		FileUtil.copyFileSimply( path, tempPath );
		
		filename = new File( path ).getName();
		pdfView.openPDF( tempPath, 0 );
		
	}//method

}//method
