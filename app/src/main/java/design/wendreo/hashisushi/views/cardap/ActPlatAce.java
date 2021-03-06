package design.wendreo.hashisushi.views.cardap;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import design.wendreo.hashisushi.R;
import design.wendreo.hashisushi.adapter.AdapterProduct;
import design.wendreo.hashisushi.dao.UserFirebase;
import design.wendreo.hashisushi.listener.RecyclerItemClickListener;
import design.wendreo.hashisushi.model.OrderItens;
import design.wendreo.hashisushi.model.Orders;
import design.wendreo.hashisushi.model.Product;
import design.wendreo.hashisushi.model.User;
import design.wendreo.hashisushi.views.ActInfo;
import design.wendreo.hashisushi.views.ActOrder;
import design.wendreo.hashisushi.views.ActPoints;
import design.wendreo.hashisushi.views.ActSignup;
import design.wendreo.hashisushi.views.ActWait;
import design.wendreo.hashisushi.views.policyPrivacy.ActPolicyPrivacy;
import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//exibe cardapio monte seu prato
public class ActPlatAce extends AppCompatActivity implements View.OnClickListener {
	
	private FloatingActionButton flotBntVoltarAce;
	private FloatingActionButton flotBntFinishAce;
	private FloatingActionButton flotBntTemakisAce;
	
	private TextView txtQuantItensAce;
	private TextView txtTotalOrderAce;
	private TextView txtCardapA;
	private TextView txtPlatAce;
	
	private DatabaseReference reference;
	private List< Product > productsList = new ArrayList< Product > ( );
	private RecyclerView lstPlaAce;
	private AdapterProduct adapterProduct;
	
	private AlertDialog dialog;
	private String retornIdUser;
	private User user;
	
	private Orders ordersRecovery;
	private int qtdItensCar;
	private Double totalCar;
	private List< OrderItens > itensCars = new ArrayList<> ( );
	
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.act_plat_ace );
		
		//getSupportActionBar().hide();
		ActionBar bar = getSupportActionBar ( );
		bar.setBackgroundDrawable ( new ColorDrawable ( Color.parseColor ( "#000000" ) ) );
		bar.setTitle ( "" );
		
		//Trav?? rota?????? da tela
		setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		
		initComponent ( );
		initDB ( );
		initSearch ( );
		retornIdUser = UserFirebase.getIdUser ( );
		fontLogo ( );
		recyclerViewConfig ( );
		recycleOnclick ( );
		recoveryDataUser ( );
		
		flotBntVoltarAce.setOnClickListener ( this );
		flotBntFinishAce.setOnClickListener ( this );
		flotBntTemakisAce.setOnClickListener ( this );
		
	}
	
	private void recycleOnclick ( ) {
		//Adiciona evento de clique no recyclerview
		lstPlaAce.addOnItemTouchListener (
				new RecyclerItemClickListener (
						this,
						lstPlaAce,
						new RecyclerItemClickListener.OnItemClickListener ( ) {
							@Override
							public void onItemClick ( View view, int position ) {
								
								Product produtoSelecionado = productsList.get ( position );
								confirmItem ( position, produtoSelecionado );
							}
							
							@Override
							public void onLongItemClick ( View view, int position ) {
							}
							
							@Override
							public void onItemClick ( AdapterView< ? > parent, View view, int position, long id ) {
							}
						}
				)
		);
		
	}
	
	private void recyclerViewConfig ( ) {
		
		//Configura recyclerview
		lstPlaAce.setLayoutManager ( new LinearLayoutManager ( this ) );
		lstPlaAce.setHasFixedSize ( true );
		adapterProduct = new AdapterProduct ( productsList, this );
		lstPlaAce.setAdapter ( adapterProduct );
		
	}
	
	@Override
	protected void attachBaseContext ( Context newBase ) {
		super.attachBaseContext ( CalligraphyContextWrapper.wrap ( newBase ) );
	}
	
	@Override
	public void onClick ( View v ) {
		
		if ( v.getId ( ) == R.id.flotBntVoltarAce ) {
			
			startVibrate ( 90 );
			finish ( );
			
		}
		if ( v.getId ( ) == R.id.flotBntFinishAce ) {
			
			startVibrate ( 90 );
			Intent it = new Intent ( this, ActOrder.class );
			startActivity ( it );
			
		} else if ( v.getId ( ) == R.id.flotBntTemakisAce ) {
			
			startVibrate ( 90 );
			openTemakis ( );
			
		}
	}
	
	//Metudo que ativa vibra????o
	public void startVibrate ( long time ) {
		// cria um obj atvib que recebe seu valor de context
		Vibrator atvib = ( Vibrator ) getSystemService ( Context.VIBRATOR_SERVICE );
		atvib.vibrate ( time );
	}
	
	//Altera fonte do txtLogo
	private void fontLogo ( ) {
		
		Typeface font = Typeface.createFromAsset ( getAssets ( ), "RagingRedLotusBB.ttf" );
		txtCardapA.setTypeface ( font );
		txtPlatAce.setTypeface ( font );
	}
	
	private void openTemakis ( ) {
		
		Intent intent = new Intent ( ActPlatAce.this, ActTemakis.class );
		//Passa efeitos de transzi????o
		ActivityOptionsCompat actcompat = ActivityOptionsCompat.makeCustomAnimation ( getApplicationContext ( ),
				R.anim.fade_in, R.anim.mover_direita );
		ActivityCompat.startActivity ( ActPlatAce.this, intent, actcompat.toBundle ( ) );
		
	}
	
	//oa clicar em voltar chama efeito de transi????o
	@Override
	public void finish ( ) {
		super.finish ( );
		overridePendingTransition ( R.anim.mover_esquerda, R.anim.fade_out );
	}
	
	public void initDB ( ) {
		FirebaseApp.initializeApp ( ActPlatAce.this );
		this.reference = FirebaseDatabase.getInstance ( ).getReference ( );
	}
	
	private void initComponent ( ) {
		
		txtQuantItensAce = findViewById ( R.id.txtQuantItensAce );
		txtTotalOrderAce = findViewById ( R.id.txtTotalOrderAce );
		flotBntVoltarAce = findViewById ( R.id.flotBntVoltarAce );
		flotBntFinishAce = findViewById ( R.id.flotBntFinishAce );
		flotBntTemakisAce = findViewById ( R.id.flotBntTemakisAce );
		
		txtCardapA = findViewById ( R.id.txtCardapA );
		txtPlatAce = findViewById ( R.id.txtPlatAce );
		
		lstPlaAce = findViewById ( R.id.LstPlatAce );
	}
	
	public void initSearch ( ) {
		//retorna usuarios
		DatabaseReference productDB = reference.child ( "product" );
		
		Query querySearch = productDB.orderByChild ( "type" ).equalTo ( "Monte_Prato" );
		
		productsList.clear ( );
		//cria um ouvinte
		querySearch.addValueEventListener ( new ValueEventListener ( ) {
			@Override
			public void onDataChange ( @NonNull DataSnapshot dataSnapshot ) {
				
				
				for ( DataSnapshot objSnapshot : dataSnapshot.getChildren ( ) ) {
					Product product = objSnapshot.getValue ( Product.class );
					productsList.add ( product );
				}
				adapterProduct.notifyDataSetChanged ( );
			}
			
			@Override
			public void onCancelled ( @NonNull DatabaseError databaseError ) {
				//msgShort ( "Houve algum erro :" + databaseError );
			}
		} );
	}
	
	private void msgShort ( String msg ) {
		
		Toast.makeText ( getApplicationContext ( ), msg, Toast.LENGTH_SHORT ).show ( );
	}
	
	//comfirmar item com dialog
	private void confirmItem ( final int position, Product produtoSelecionado ) {
		AlertDialog.Builder alert = new AlertDialog.Builder ( this );
		alert.setTitle ( produtoSelecionado.getName ( ) );
		alert.setMessage ( "\nInforme a quantidade desejada: " );
		
		final EditText edtQuant = new EditText ( this );
		edtQuant.setText ( "1" );
		
		alert.setView ( edtQuant );
		alert.setPositiveButton ( "Confirmar", new DialogInterface.OnClickListener ( ) {
			@Override
			public void onClick ( DialogInterface dialog, int which ) {
				String quantity = edtQuant.getText ( ).toString ( );
				//trata erro de qt vazia
				if ( quantity.equals ( "" ) ) {
					quantity = "1";
					msgShort ( "Voc?? n??o definiu Quantidade !" );
					msgShort ( "um item foi adicionado automaticamente." );
				}
				
				if ( validaQuantidade ( quantity ) == 0 ) {
					
					Product productSelectd = productsList.get ( position );
					
					OrderItens itemOrder = new OrderItens ( );
					
					itemOrder.setIdProduct ( productSelectd.getIdProd ( ) );
					itemOrder.setNameProduct ( productSelectd.getName ( ) );
					itemOrder.setItenSalePrice ( productSelectd.getSalePrice ( ) );
					itemOrder.setQuantity ( Integer.parseInt ( quantity ) );
					
					itensCars.add ( itemOrder );
					msgShort ( "Produto adicionado ao seu carrinho!" );
					
					// msgShort(itensCars.toString());
					
					if ( ordersRecovery == null ) {
						ordersRecovery = new Orders ( retornIdUser );
					}
					ordersRecovery.setName ( user.getName ( ) );
					ordersRecovery.setAddress ( user.getAddress ( ) );
					ordersRecovery.setNeigthborhood ( user.getNeigthborhood ( ) );
					ordersRecovery.setNumberHome ( user.getNumberHome ( ) );
					ordersRecovery.setCellphone ( user.getPhone ( ) );
					ordersRecovery.setOrderItens ( itensCars );
					
					ordersRecovery.salvar ( );
					
				} else {
					edtQuant.setText ( "1" );
				}
			}
		} );
		
		alert.setNegativeButton ( "Cancelar", new DialogInterface.OnClickListener ( ) {
			@Override
			public void onClick ( DialogInterface dialog, int which ) {
			}
		} );
		AlertDialog dialog = alert.create ( );
		dialog.show ( );
	}
	
	private int validaQuantidade ( String valor ) //valida se o valor digitado ?? num??rico
	{
		String regexStr = "^[0-9]*$";
		if ( !valor.trim ( ).matches ( regexStr ) ) {
			msgShort ( "Por favor, informe um valor num??rico!" );
			return 1;
		} else return 0;
	}
	
	//recupera dados do usuario esta com
	// proplema para recuperar user
	private void recoveryDataUser ( ) {
		
		dialog = new SpotsDialog.Builder ( )
				.setContext ( this )
				.setMessage ( "Carregando dados aguarde...." )
				.setCancelable ( false )
				.build ( );
		dialog.show ( );
		
		
		DatabaseReference usuariosDB = reference.child ( "users" ).child ( retornIdUser );
		
		usuariosDB.addListenerForSingleValueEvent ( new ValueEventListener ( ) {
			@Override
			public void onDataChange ( DataSnapshot dataSnapshot ) {
				
				if ( dataSnapshot.getValue ( ) != null ) {
					
					user = dataSnapshot.getValue ( User.class );
				}
				recoveryOrder ( );
			}
			
			@Override
			public void onCancelled ( DatabaseError databaseError ) {}
		} );
		
	}
	
	//recupera pedido
	private void recoveryOrder ( ) {
		
		DatabaseReference pedidoRef = reference
				.child ( "orders_user" )
				.child ( retornIdUser );
		
		pedidoRef.addValueEventListener ( new ValueEventListener ( ) {
			@Override
			public void onDataChange ( DataSnapshot dataSnapshot ) {
				
				qtdItensCar = 0;
				totalCar = 0.0;
				itensCars = new ArrayList<> ( );
				
				
				if ( dataSnapshot.getValue ( ) != null ) {
					ordersRecovery = dataSnapshot.getValue ( Orders.class );
					
					//trata null pointer apos
					// remover untimo iten carrinho
					if ( ordersRecovery != null ) {
						
						itensCars = ordersRecovery.getOrderItens ( );
						
					} else {
						Orders orders = new Orders ( );
						orders.removerOrderItens ( retornIdUser );
					}
					//trata NullPointer
					if ( itensCars != null ) {
						
						for ( OrderItens orderItens : itensCars ) {
							int qtde = orderItens.getQuantity ( );
							
							double preco = Double.parseDouble ( orderItens.getItenSalePrice ( ) );
							
							totalCar += ( qtde * preco );
							qtdItensCar += qtde;
						}
					} else {
						
						Orders orders = new Orders ( );
						orders.removerOrderItens ( retornIdUser );
					}
				}
				
				
				DecimalFormat df = new DecimalFormat ( "0.00" );
				
				txtQuantItensAce.setText ( String.valueOf ( qtdItensCar ) );
				txtTotalOrderAce.setText ( df.format ( totalCar ) );
				
				dialog.dismiss ( );
				
			}
			
			@Override
			public void onCancelled ( DatabaseError databaseError ) {
				//msgShort ( "ERRO ao carregar ERRO:" + databaseError );
			}
		} );
	}
	
	//==> MENUS
	@Override
	public boolean onCreateOptionsMenu ( Menu menu ) {
		getMenuInflater ( ).inflate ( R.menu.menu_promotion, menu );
		return true;
	}
	
	@TargetApi ( Build.VERSION_CODES.LOLLIPOP )
	@RequiresApi ( api = Build.VERSION_CODES.LOLLIPOP )
	@Override
	public boolean onOptionsItemSelected ( MenuItem item ) {
		int id = item.getItemId ( );
		
		if ( id == R.id.menu_enter ) {
			Intent it = new Intent ( this, ActSaleCardap.class );
			startActivity ( it );
			finish ( );
			return true;
		}
		
		if ( id == R.id.menu_plat_hot ) {
			Intent it = new Intent ( this, ActPlatHot.class );
			startActivity ( it );
			finish ( );
			return true;
		}
		
		if ( id == R.id.menu_plat_ace ) {
			Intent it = new Intent ( this, ActPlatAce.class );
			startActivity ( it );
			finish ( );
			return true;
		}
		
		if ( id == R.id.menu_combo ) {
			Intent it = new Intent ( this, ActCombo.class );
			startActivity ( it );
			finish ( );
			return true;
		}
		
		if ( id == R.id.menu_drinks ) {
			Intent it = new Intent ( this, ActDrinks.class );
			startActivity ( it );
			finish ( );
			return true;
		}
		if ( id == R.id.menu_temakis ) {
			Intent it = new Intent ( this, ActTemakis.class );
			startActivity ( it );
			finish ( );
			return true;
		}
		if ( id == R.id.menu_edit_cadastro ) {
			Intent it = new Intent ( this, ActSignup.class );
			startActivity ( it );
			finish ( );
			return true;
		}
		if ( id == R.id.menu_points ) {
			Intent it = new Intent ( this, ActPoints.class );
			startActivity ( it );
			finish ( );
			return true;
		}
		if ( id == R.id.menu_satus ) {
			Intent it = new Intent ( this, ActWait.class );
			startActivity ( it );
			finish ( );
			return true;
		}
		
		if ( id == R.id.menu_addional ) {
			Intent it = new Intent ( this, ActAdditional.class );
			startActivity ( it );
			return true;
		}
		
		if(id == R.id.menu_edit_about){
			Intent it = new Intent ( this, ActInfo.class );
			startActivity ( it );
			return true;
		}
		
		return super.onOptionsItemSelected ( item );
	}
	//==>END MENUS
	
}
