package design.wendreo.hashisushi.views;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import design.wendreo.hashisushi.R;
import design.wendreo.hashisushi.utils.data.SecurityPreferences;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActLogin extends AppCompatActivity implements View.OnClickListener {
	
	private Button btnEntrar, btnCadastrar;
	;
	private TextView txtLogo;
	private EditText edtEmail, edtSenha;
	private String senha, email;
	private int cont;
	private char controlBtn;
	private ConstraintLayout ActLogin;
	private SecurityPreferences shared;
	private Switch chkBxRememberPasswd;
	private String emailUser;
	private FirebaseAuth userAuth;
	private DatabaseReference reference;
	
	
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.act_login );
		
		this.shared = new SecurityPreferences ( this );
		getSupportActionBar ( ).hide ( );
		
		
		setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );  //Trava a rota?????? da tela
		
		findViewByIds ( );
		fontLogo ( );  //Chama m??todo que altera fonte logo
		
		emailUser = this.shared.getStoredString ( "EmailUserSaved" );
		setEmailUser ( );
		
		if ( !edtEmail.getText ( ).toString ( ).equals ( "" ) ) {
			edtSenha.requestFocus ( );
		}
		
		startDB ( );
	}
	
	public void startDB ( ) {
		FirebaseApp.initializeApp ( ActLogin.this );
		this.reference = FirebaseDatabase.getInstance ( ).getReference ( );
		this.userAuth = FirebaseAuth.getInstance ( );
	}
	
	@Override
	protected void attachBaseContext ( Context newBase ) {
		super.attachBaseContext ( CalligraphyContextWrapper.wrap ( newBase ) );
	}
	
	private void fontLogo ( ) //Altera fonte do txtLogo
	{
		Typeface font = Typeface.createFromAsset ( getAssets ( ), "RagingRedLotusBB.ttf" );
		txtLogo.setTypeface ( font );
	}
	
	@Override
	protected void onResume ( ) {
		super.onResume ( );
		this.setEmailUser ( );  //Carrega o e-mail de SharedPreferences
	}
	
	@Override
	public void onClick ( View v ) {
		if ( v.getId ( ) == R.id.btnEntrar ) {
			ShowMSG ( "?????????????????????? ????????....." );
			
			controlBtn = 'E';
			startVibrate ( 90 );
			validateFields ( );
		} else if ( v.getId ( ) == R.id.btnCadastrar ) {
			//-----------------
			controlBtn = 'C';
			startVibrate ( 90 );
			iniciarCadastro ( );
			//validateFields();
		} else if ( v.getId ( ) == R.id.chkBxRememberPasswd ) {
			if ( chkBxRememberPasswd.isChecked ( ) ) {
				this.shared.storeString ( "EmailUserSaved", edtEmail.getText ( ).toString ( ) );
				//msgShort(emailUser);
				ShowMSG ( getString ( R.string.email_rememberd ) );
			} else {
				this.shared.storeString ( "EmailUserSaved", "" );
				ShowMSG ( getString ( R.string.email_not_rememberd ) );
			}
		}
	}
	
	//login user in firebase
	public void login ( String email, String senha ) {
		userAuth.signInWithEmailAndPassword ( email, senha )
				.addOnCompleteListener ( ActLogin.this, new OnCompleteListener< AuthResult > ( ) {
					@Override
					public void onComplete ( @NonNull Task< AuthResult > task ) {
						if ( task.isSuccessful ( ) ) {
							msgShort ( getString ( R.string.welcome ) );
							initPromotion ( );
						} else {
							msgShort ( getString ( R.string.error_to_access ) );
							//desloga
							userAuth.signOut ( );
						}
					}
				} );
	}
	
	//create user in firebase
	public void addUserLogin ( String email, String senha ) {
		userAuth.createUserWithEmailAndPassword ( email, senha )
				.addOnCompleteListener ( ActLogin.this, new OnCompleteListener< AuthResult > ( ) {
					@Override
					public void onComplete ( @NonNull Task< AuthResult > task ) {
						if ( task.isSuccessful ( ) ) {
							msgShort ( "?????????? ??????????????????..." );
							msgShort ( "???????? ??????????????????!" );
							initSignup ( );
						} else {
							msgShort ( "?? ?????????????????? ?????????????????????? ???? ???????? ?????????????????? :(" );
							Log.i ( "Erro", "Infelizmente n??o foi poss??vel concluir o cadastro :(" );
							//desloga
							userAuth.signOut ( );
						}
					}
				} );
	}
	
	private void initPromotion ( ) {
		Intent it = new Intent ( this, ActPromotion.class );
		startActivity ( it );
	}
	
	private void initSignup ( ) {
		Intent it = new Intent ( this, ActSignup.class );
		startActivity ( it );
	}
	
	//M??todo que ativa vibra????o
	public void startVibrate ( long time ) {
		// cria um obj atvib que recebe seu valor de context
		Vibrator atvib = ( Vibrator ) getSystemService ( Context.VIBRATOR_SERVICE );
		atvib.vibrate ( time );
	}
	
	public void validateFields ( ) {
		email = edtEmail.getText ( ).toString ( );
		senha = edtSenha.getText ( ).toString ( );
		
		if ( cont <= 3 ) {
			if ( email.trim ( ).isEmpty ( ) || senha.trim ( ).isEmpty ( ) ) {
				cont++;
				ShowMSG ( getString ( R.string.type_email_and_pass ) );
			} else {
				if ( controlBtn == 'E' ) {
					login ( email, senha );
					
				}
				if ( controlBtn == 'C' ) {
					//addUserLogin(email,senha);
					
				}
				clearFields ( );
				cont = 0;
			}
		} else {
			finaliza ( );
		}
	}
	
	
	private void finaliza ( ) {
		msgShort ( getString ( R.string.app_finished ) );
		finish ( );
	}
	
	private void msgShort ( String msg ) {
		Toast.makeText ( getApplicationContext ( ), msg, Toast.LENGTH_SHORT ).show ( );
	}
	
	private void clearFields ( ) {
		edtEmail.setText ( "" );
		edtSenha.setText ( "" );
	}
	
	private void ShowMSG ( String msg ) {
		Snackbar.make ( ActLogin, msg, Snackbar.LENGTH_LONG ).show ( );
	}
	
	private void setEmailUser ( ) {
		if ( !emailUser.equals ( "" ) ) {
			edtEmail.setText ( emailUser );
			chkBxRememberPasswd.setChecked ( true );
		} else {
			edtEmail.setText ( emailUser );
		}
	}
	
	private void findViewByIds ( ) {
		btnEntrar = findViewById ( R.id.btnEntrar );
		btnCadastrar = findViewById ( R.id.btnCadastrar );
		txtLogo = findViewById ( R.id.txtLogoC );
		edtEmail = findViewById ( R.id.edtEmail );
		edtSenha = findViewById ( R.id.edtSenha );
		ActLogin = findViewById ( R.id.ActLogin );
		chkBxRememberPasswd = findViewById ( R.id.chkBxRememberPasswd );
		btnCadastrar.setOnClickListener ( this );
		btnEntrar.setOnClickListener ( this );
		chkBxRememberPasswd.setOnClickListener ( this );
		
	}
	
	
	// dialog
	private void iniciarCadastro ( ) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder ( this );
		alert.setIcon ( R.drawable.ic_warning_yello_24dp );
		alert.setTitle ( "???????????????? !" );
		alert.setMessage ( "\n?????????? ???????????????????????????????? ?????????????? ???????????? ?????????????? ???????? ?????????? ?????????????????????? ?????????? ?? ???????????????????? ????????????" );
		
		
		final EditText edtEmail = new EditText ( this );
		final EditText edtPassword = new EditText ( this );
		
		LinearLayout layoutFilds = new LinearLayout ( this );
		layoutFilds.setOrientation ( LinearLayout.VERTICAL );
		
		layoutFilds.addView ( edtEmail );
		edtEmail.setHint ( "?????????????? ?????? ??-mail" );
		
		layoutFilds.addView ( edtPassword );
		edtPassword.setHint ( "?????????????? ?????? ????????????" );
		
		alert.setView ( layoutFilds );
		
		
		alert.setPositiveButton ( "???????????????????? ??????????????????????", new DialogInterface.OnClickListener ( ) {
			@Override
			public void onClick ( DialogInterface dialog, int which ) {
				String email = edtEmail.getText ( ).toString ( );
				String password = edtPassword.getText ( ).toString ( );
				
				if ( email.equals ( "" ) || password.equals ( "" ) ) {
					
					msgShort ( "????????????????, ?????????????????? ????????!" );
					
					if ( email.equals ( "" ) ) {
						msgShort ( "?????????????? ???????????????????????????? ?????????? ??????????!" );
					} else if ( password.equals ( "" ) ) {
						msgShort ( "?????????????? ????????????!" );
					}
					
				} else {
					addUserLogin ( email, password );
				}
			}
		} );
		
		alert.setNegativeButton ( "??????????", new DialogInterface.OnClickListener ( ) {
			@Override
			public void onClick ( DialogInterface dialog, int which ) {
			
			}
		} );
		AlertDialog dialog = alert.create ( );
		dialog.show ( );
	}
}