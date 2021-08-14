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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


public class Result extends AppCompatActivity {
    private EditText editText1;
    private Spinner spinner, spinner2;
    private TextInputEditText sourceEdt;
    private TextView textView, langTextv;
    private TextInputLayout textInputLayout;
    private Button btntrans, btnident;
    String fromLanguages[] = {"From", "English"};
    String toLanguages[] = {"To", "English", "Hindi", "Marathi", "Tamil"};
    int LanguageCode, LangCode = 0, toLanguageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        spinner2 = findViewById(R.id.spinner2);
        sourceEdt = findViewById(R.id.sourcedt);
        btntrans = findViewById(R.id.transBtn);
        editText1 = findViewById(R.id.EditText);
        textView = findViewById(R.id.translated);
        langTextv = findViewById(R.id.Lang);
        Intent intent = getIntent();
        String str1 = intent.getStringExtra("mess");
        sourceEdt.setText(str1);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode = getLanguageCode(toLanguages[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(toAdapter);
        btntrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
                if (sourceEdt.getText().toString().isEmpty()) {
                    Toast.makeText(Result.this, "Text not Present", Toast.LENGTH_SHORT).show();
                } else if (toLanguageCode == 0) {
                    Toast.makeText(Result.this, "Select Destination Language", Toast.LENGTH_SHORT).show();
                } else {
                    identifyLanguage();                }
            }
        });

    }


    private void translateText(int LangCode, int toLanguageCode, String source) {
        System.out.println(LangCode);
        textView.setText("Downloading Model...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(LangCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
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
                        Toast.makeText(Result.this, "Failed to Translate" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Result.this, "Failed to Download Model" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void identifyLanguage() {
        textView.setText("Identifying Language...");
        if (sourceEdt.getText().toString().isEmpty()) {
            Toast.makeText(Result.this, "Text not Present", Toast.LENGTH_SHORT).show();
        }
        String text = sourceEdt.getText().toString();
        FirebaseLanguageIdentification identifer = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        identifer.identifyLanguage(text).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("und")) {
                    Toast.makeText(Result.this, "Language not identifield", Toast.LENGTH_SHORT).show();
                } else {
                    getLangCode(s);
                }
            }
        });
    }
    private void getLangCode(String language) {
        int LangCode1=0;
        switch (language){
            case "hi":
                LangCode1=FirebaseTranslateLanguage.HI;
                langTextv.setText("Hindi");
                break;
            case "en":
                LangCode1=FirebaseTranslateLanguage.EN;
                langTextv.setText("English");
                break;
            case "mr":
                LangCode1=FirebaseTranslateLanguage.MR;
                langTextv.setText("Marathi");
                break;
            case "es":
                LangCode1=FirebaseTranslateLanguage.ES;
                langTextv.setText("Spanish");
                break;
            case "de":
                LangCode1=FirebaseTranslateLanguage.DE;
                langTextv.setText("Germen");
                break;
            case "fr":
                LangCode1=FirebaseTranslateLanguage.FR;
                langTextv.setText("French");
                break;
            case "nl":
                LangCode1=FirebaseTranslateLanguage.NL;
                langTextv.setText("Dutch");
                break;
            case "ru":
                LangCode1=FirebaseTranslateLanguage.RU;
                langTextv.setText("Russian");
                break;
            case "ta":
                LangCode1=FirebaseTranslateLanguage.TA;
                langTextv.setText("Tamil");
                break;
            case "ur":
                LangCode1=FirebaseTranslateLanguage.UR;
                langTextv.setText("Urdu");
                break;
            case "gu":
                LangCode1=FirebaseTranslateLanguage.GU;
                langTextv.setText("Gujarati");
                break;
            case "id":
                LangCode1=FirebaseTranslateLanguage.ID;
                langTextv.setText("Indonesian");
                break;
            case "ja":
                LangCode1=FirebaseTranslateLanguage.JA;
                langTextv.setText("Japanese");
                break;
            case "el":
                LangCode1=FirebaseTranslateLanguage.EL;
                langTextv.setText("Greek");
                break;
            case "ar":
                LangCode1=FirebaseTranslateLanguage.AR;
                langTextv.setText("Arabic");
                break;
            case "bn":
                LangCode1=FirebaseTranslateLanguage.BN;
                langTextv.setText("Bengali");
                break;
            case "sv":
                LangCode1=FirebaseTranslateLanguage.SV;
                langTextv.setText("Swedish");
                break;
            case "pt":
                LangCode1=FirebaseTranslateLanguage.PT;
                langTextv.setText("Portuguese");
                break;
            default:
                LangCode1=0;

        }
        translateText(LangCode1, toLanguageCode, sourceEdt.getText().toString());
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