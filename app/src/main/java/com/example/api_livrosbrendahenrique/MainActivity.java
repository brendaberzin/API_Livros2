package com.example.api_livrosbrendahenrique;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private EditText nomeLivro;
    private TextView nomeTitulo;
    private TextView nomeAutor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        nomeLivro = findViewById(R.id.InputLivro);
        nomeTitulo = findViewById(R.id.titulo);
        nomeAutor = findViewById(R.id.autor);
        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }
    public void buscaLivros(View view) {
        String queryString = nomeLivro.getText().toString();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()
                && queryString.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            nomeAutor.setText(R.string.vazio);
            nomeTitulo.setText(R.string.carregando);
        } else {
            if (queryString.length() == 0) {
                nomeAutor.setText(R.string.vazio);
                nomeTitulo.setText(R.string.campo_null);
            } else {
                nomeAutor.setText(" ");
                nomeTitulo.setText(R.string.sem_internet);
            }
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";
        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new LoadLivros(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray itemsArray = jsonObject.getJSONArray("ISBN:0451526538");
            int i = 0;
            String titulo = null;
            String autor = null;

            while (i < itemsArray.length() &&
                    (titulo == null)) {
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject authors = book.getJSONObject("authors");

                try {
                    titulo = book.getString("title");
                    autor = authors.getString("name");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                i++;
            }
            if (titulo != null) {
                nomeTitulo.setText(titulo);
//                autor = autor.replaceAll("\\\"", "");
                nomeAutor.setText(autor);
            } else {
                nomeTitulo.setText(R.string.campo_null);
                nomeAutor.setText(R.string.vazio);
                Toast.makeText(getApplicationContext(),"else", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            nomeTitulo.setText(R.string.vazio);
            nomeAutor.setText(R.string.vazio);
            Toast.makeText(getApplicationContext(),"Nenhum livro encontrado", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }
}