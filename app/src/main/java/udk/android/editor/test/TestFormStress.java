package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.GlobalConfigurationService;
import udk.android.reader.view.pdf.PDFView;
import udk.android.reader.view.pdf.PDFViewAdapter;
import udk.android.reader.view.pdf.PDFViewEvent;
import udk.android.util.CloseUtil;
import udk.android.util.FileUtil;
import udk.android.util.LogUtil;
import udk.android.util.RandomUtil;
import udk.android.util.ThreadUtil;
import udk.android.util.Workable;
import udk.android.widget.Alerter;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestFormStress extends TestBase{
	
	private static List< byte[] > cache = new ArrayList< byte[] >();
	private int idx;
	
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
		getParentContainer().removeAllViews();
		
		final PDFView pv = new PDFView( this );
		RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams( 100, 100 );
		getParentContainer().addView( pdfView );
		
		FileUtil.copyFileSimply( "/sdcard/test/IRP_case1_Form.pdf", "/sdcard/test/formtestsdf.pdf" );
		pv.openPDF( "/sdcard/test/formtestsdf.pdf", RandomUtil.getRandomInFromTo( 1, 16 ), RandomUtil.getRandomInFromTo( 1, 16 ) );
		
		repeat();
	}//method

	private void repeat(){
		
		final PDFView pdfView = new PDFView( this );
		RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams( getPDFViewWidth(), getPDFViewHeight() );
		getParentContainer().addView( pdfView );
		
		pdfView.setOnPDFReadyListener( new PDFView.OnPDFReadyListener(){
			@Override
			public void onPDFReady(){
				FileInputStream fis = null;
				try{
					fis = new FileInputStream( new File( "/sdcard/test/empty.pdf" ) );
					boolean b = pdfView.putUserData( "dummydata1", fis );
					if( !b ){
						throw new Exception( "PUT USERDATA FAILURE" );
					}
				}catch( Exception ex ){
					LogUtil.e( ex );
				}finally{
					CloseUtil.close( fis );
				}
			}
		} );
		pdfView.setPDFViewListener( new PDFViewAdapter(){
			public void onOpenCompleted( PDFViewEvent e ){
				new Thread(){
					public void run(){
						ThreadUtil.sleepQuietly( 1000 );
						FileInputStream fis = null;
						try{
							
							ThreadUtil.sleepQuietly( 1000 );
							pdfView.page( RandomUtil.getRandomInFromTo( 1, 16 ) );
							
							fis = new FileInputStream( new File( "/sdcard/test/empty.pdf" ) );
							boolean b = pdfView.putUserData( "dummydata2", fis );
							if( !b ){
								throw new Exception( "PUT USERDATA FAILURE" );
							}
							CloseUtil.close( fis );
							
							pdfView.page( RandomUtil.getRandomInFromTo( 1, 16 ) );
							
							fis = new FileInputStream( new File( "/sdcard/test/empty.pdf" ) );
							b = pdfView.putUserData( "dummydata3", fis );
							if( !b ){
								throw new Exception( "PUT USERDATA FAILURE" );
							}
							CloseUtil.close( fis );
							
							pdfView.page( RandomUtil.getRandomInFromTo( 1, 16 ) );
							
							fis = new FileInputStream( new File( "/sdcard/test/empty.pdf" ) );
							b = pdfView.putUserData( "dummydata4", fis );
							if( !b ){
								throw new Exception( "PUT USERDATA FAILURE" );
							}
						}catch( Exception ex ){
							LogUtil.e( ex );
						}finally{
							CloseUtil.close( fis );
						}
						pdfView.closePDF();

						runOnUiThread( new Runnable(){
							@Override
							public void run(){
								getParentContainer().removeAllViews();
								repeat();
							}
						} );

					}//method
				}.start();
			}//method
		} );
		FileUtil.copyFileSimply( "/sdcard/test/IRP_case1_Form.pdf", "/sdcard/test/formtest.pdf" );
		pdfView.openPDF( "/sdcard/test/formtest.pdf", RandomUtil.getRandomInFromTo( 1, 16 ), RandomUtil.getRandomInFromTo( 1, 16 ) );
		Alerter.shortNotice( TestFormStress.this, "OPEN ## " + idx++ );
	}
	
}//method
