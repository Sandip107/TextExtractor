package com.example.textscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class Result extends AppCompatActivity {
    private EditText editText1;
    private Spinner spinner,spinner2;
    private TextInputEditText sourceEdt;
    private TextView textView;
    private TextInputLayout textInputLayout;
    private Button btntrans;
    String fromLanguages[]={"From","English"};
    String toLanguages[]={"To","Hindi","Marathi","Tamil"};
    int LanguageCode,fromLanguageCode,toLanguageCode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        spinner2=findViewById(R.id.spinner2);
        sourceEdt=findViewById(R.id.sourcedt);
        btntrans=findViewById(R.id.transBtn);
        editText1=findViewById(R.id.EditText);
        spinner=findViewById(R.id.spinner);
        textView=findViewById(R.id.translated);
        Intent intent=getIntent();
        String str1=intent.getStringExtra("mess");
        sourceEdt.setText(str1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode=getLanguageCode(fromLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromAdapter=new ArrayAdapter(this,R.layout.spinner_item,fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(fromAdapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode=getLanguageCode(toLanguages[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter toAdapter=new ArrayAdapter(this,R.layout.spinner_item,toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(toAdapter);
        btntrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
                if (sourceEdt.getText().toString().isEmpty()){
                    Toast.makeText(Result.this,"Text not Present",Toast.LENGTH_SHORT).show();
                }else if (fromLanguageCode==0){
                    Toast.makeText(Result.this,"Select Source Language",Toast.LENGTH_SHORT).show();
                }else if (toLanguageCode==0){
                    Toast.makeText(Result.this,"Select Destination Language",Toast.LENGTH_SHORT).show();
                }else {
                    translateText(fromLanguageCode,toLanguageCode,sourceEdt.getText().toString());
                }
            }
        });

    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source) {
        textView.setText("Downloding Model.....");
        FirebaseTranslatorOptions options=new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator= FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions  conditions=new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                textView.setText("translating......");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        textView.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Result.this,"Failed to Translate"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Result.this,"Failed to Download Model"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private int getLanguageCode(String language) {
        int languageCode=0;
        switch (language){
            case "English":
                languageCode= FirebaseTranslateLanguage.EN;
                break;
            case "Hindi":
                languageCode= FirebaseTranslateLanguage.HI;
                break;
            case "Marathi":
                languageCode= FirebaseTranslateLanguage.MR;
                break;
            case "Tamil":
                languageCode= FirebaseTranslateLanguage.TA;
                break;
            default:
                languageCode=0;
        }
        return languageCode;
    }
}