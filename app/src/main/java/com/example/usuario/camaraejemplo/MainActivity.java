package com.example.usuario.camaraejemplo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button captura;
    Button captura2;
    ImageView imageViewfoto;
    static  int VENGO_DE_LA_CAMARA=1;
    static  int VENGO_DE_LA_CAMARA_CON_FICHERO=2;
    static final int PEDIR_PERMISOS_PARA_ESCRIBIR=1;
    String nombre_fichero;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captura=findViewById(R.id.buttonCaptura);
        captura2=findViewById(R.id.Captura2);
        imageViewfoto=findViewById(R.id.imageViewFoto);

        captura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent hacerFoto =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(hacerFoto.resolveActivity(getPackageManager())!=null)
                {
                    startActivityForResult(hacerFoto,VENGO_DE_LA_CAMARA);
                }else{
                    Toast.makeText(MainActivity.this, "Necesito un programa para hacer fotos.", Toast.LENGTH_SHORT).show();

                }
            }
        });


        //BOTON CAPTURA 2.
        captura2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perdirPermisoDeEscrituraYhagoFoto();


            }
        });
    }
    //metodo para hacer fotos
    void haceFoto()
    {
        Intent hacerFoto=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(hacerFoto.resolveActivity(getPackageManager())!=null)
        {
            //Le voy a decir a la aplicacion de la foto, que me guarde las fotos en el fichero.
            File ficheroFoto=null;
            try {
                ficheroFoto=crearFicheroDeImagen();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(ficheroFoto!=null)
            {
                //cuando haga la foto la guarde en ese fichero.
                hacerFoto.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(ficheroFoto));
                startActivityForResult(hacerFoto,VENGO_DE_LA_CAMARA_CON_FICHERO);
            }

        }else{
            Toast.makeText(MainActivity.this, "Necesito un programa para hacer fotos.", Toast.LENGTH_SHORT).show();

        }
    }
    //metodo para comprobar permisos.
    void perdirPermisoDeEscrituraYhagoFoto()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PEDIR_PERMISOS_PARA_ESCRIBIR);
            }

        }else{
                haceFoto();
            //está concedido el permiso de escritura
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      switch (requestCode)
      {
          case PEDIR_PERMISOS_PARA_ESCRIBIR:
              if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
              {
                  haceFoto();

              }else{
                  Toast.makeText(this, "Sin permisos, no funciona.", Toast.LENGTH_SHORT).show();
              }
              return;
      }
    }

    //METODO QUE ME CREE UN FICHERO DE IMAGEN.
    File crearFicheroDeImagen() throws IOException {

        //Me va a crear un fichero vacio, con la fecha de hoy.
        String fecha=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero="MisFotos_"+fecha;
        //Entorno-->Environment.
        File carpetaDeFotos=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image =File.createTempFile(nombreFichero,".jpg",carpetaDeFotos);
        return  image;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //Me comprueba que vengo de la camara.
        if((requestCode==VENGO_DE_LA_CAMARA )&&(resultCode==RESULT_OK))
        {
            //Bundle es un envoltorio.
                Bundle extras=data.getExtras();
                Bitmap foto= (Bitmap) extras.get("data");
                imageViewfoto.setImageBitmap(foto);
        }
        //Me comprueba que vengo de la camara.
        if((requestCode==VENGO_DE_LA_CAMARA_CON_FICHERO )&&(resultCode==RESULT_OK))
        {
            //Bundle es un envoltorio.
            Bundle extras=data.getExtras();
            Bitmap foto= (Bitmap) extras.get("data");
            imageViewfoto.setImageBitmap(foto);
        }


    }


}
