package udk.android.editor.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import udk.android.editor.view.pdf.PDFView;
import udk.android.util.LogUtil;

public class TestXmlLayout extends Activity{
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		
		LogUtil.DEBUG = true;
		
		setContentView( 3 );
		final PDFView pdfView = ( PDFView ) findViewById( 3 );
		
		findViewById( 3 ).setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				pdfView.setVisibility( View.VISIBLE );
				pdfView.openPDF( "https://manuals.info.apple.com/MANUALS/1000/MA1595/en_US/ipad_user_guide.pdf", 1 );
			}
		} );
	}
}
