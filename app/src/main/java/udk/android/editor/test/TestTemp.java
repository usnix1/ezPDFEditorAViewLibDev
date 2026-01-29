package udk.android.editor.test;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;

import java.io.File;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.PDFView;
import udk.android.reader.R;
import udk.android.util.Workable;

public class TestTemp extends TestBase{

	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				LibConfiguration.REQUEST_AS_PROGRAM_DATA_ROOT = new File( "/sdcard/test/temp" );
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		
		final Context context = this;
		
		return new Workable< View >(){
			@Override
			public void work( View tool ){

			}//method
		};
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		pdfView = getPDFView();
		pdfView.setLoadingAnimation( ( AnimationDrawable ) getResources().getDrawable( R.anim.loading ) );
		pdfView.openPDF( "/sdcard/test/21-1-1-ma-2-1534253791-dt.pdf", 4 );		
	}//method
	
}
