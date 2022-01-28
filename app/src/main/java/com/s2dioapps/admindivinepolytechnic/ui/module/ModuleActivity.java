package com.s2dioapps.admindivinepolytechnic.ui.module;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.s2dioapps.admindivinepolytechnic.R;
import com.s2dioapps.admindivinepolytechnic.common.DbQuery;
import com.s2dioapps.admindivinepolytechnic.common.NodeNames;
import com.s2dioapps.admindivinepolytechnic.ui.lesson.LessonFragment;
import com.s2dioapps.admindivinepolytechnic.ui.login.LoginActivity;
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectFragment;
import com.s2dioapps.admindivinepolytechnic.ui.test.TestActivity;
import com.s2dioapps.admindivinepolytechnic.ui.test.TestAdapter;
import com.s2dioapps.admindivinepolytechnic.ui.test.TestModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleActivity extends AppCompatActivity {

    private RecyclerView moduleView;
    private Button addModuleB;
    private ModuleAdapter adapter;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;
    public static int selected_test_index=0;

    public static List<ModuleModel> moduleList = new ArrayList<>();

    public static int ctrModule = 0;

    Dialog addModuleDialog;
    private EditText dialogModuleName;
    private EditText dialogModuleTime;
    private Button dialogAddB;

    StorageReference storageReference;

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        Toolbar toolbar = findViewById(R.id.module_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Modules");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        moduleView = findViewById(R.id.module_recycler);
        addModuleB = findViewById(R.id.addModuleB);

        loadingDialog = new Dialog(ModuleActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addModuleDialog = new Dialog(this);
        addModuleDialog.setContentView(R.layout.add_sub_dialog);
        addModuleDialog.setCancelable(true);
        addModuleDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogModuleName = addModuleDialog.findViewById(R.id.ac_cat_name);
        dialogAddB = addModuleDialog.findViewById(R.id.ac_add_btn);

        addModuleB.setText("ADD NEW MODULE");
        dialogAddB.setText("Upload Pdf");
        dialogModuleName.setHint("Module Name");

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        addModuleB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogModuleName.getText().clear();
                addModuleDialog.show();

            }
        });

        dialogAddB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogModuleName.getText().toString().isEmpty())
                {
                    dialogModuleName.setError("Enter Module Name");
                    return;
                }
                UploadPdf();


            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        moduleView.setLayoutManager(layoutManager);

        loadModules();

        ctrModule = LessonFragment.lessonList.get(LessonFragment.selected_lesson_index).getNoOfModules();


    }

    private void UploadPdf()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"), PICK_PDF_FILE);
        //addNewModule(dialogModuleName.getText().toString());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_FILE) {
            if (resultCode == RESULT_OK) {
                PutFilePdf(data.getData(),dialogModuleName.getText().toString().trim());
                //Log.e("PDF", String.valueOf(data.getData()));
//                localFileUri = data.getData();
//                addNewModule("Sample");
                //ivProfile.setImageURI(localFileUri);
            }

        }
    }

    private void PutFilePdf(Uri data, String title)
    {
        addModuleDialog.dismiss();
        loadingDialog.show();

        final StorageReference fileRef = storageReference.child("pdf/"+ System.currentTimeMillis()+".pdf");

        fileRef.putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            WriteBatch batch = firestore.batch();

                            DocumentReference lessonDoc = DbQuery.g_firestore.collection("Lessons")
                                    .document(LessonFragment.lessonList.get(LessonFragment.selected_lesson_index).getLe_id());

                            Map<String,Object> moduleInfo = new ArrayMap<>();

                            Map<String,Object> modDoc = new ArrayMap<>();
                            modDoc.put("SUB" + (LessonFragment.selected_lesson_index + 1) + "_NO_OF_MODULES",
                                    ++ctrModule);

                            moduleInfo.put("MODULE" + ctrModule + "_ID",title);
                            moduleInfo.put("MODULE" + ctrModule + "_PDF",uri.toString());
                            LessonFragment.lessonList.get(LessonFragment.selected_lesson_index).setNoOfModules(ctrModule);

                            DocumentReference moduleField = lessonDoc.collection("MODULE_LIST").document("MODULE_INFO");

                            batch.set(moduleField, moduleInfo, SetOptions.merge());

                            DbQuery.g_firestore.collection("Lessons")
                                    .document("SUBJECTS")
                                    .update(modDoc)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            moduleList.add(new ModuleModel(title,uri.getPath()));

                                            adapter.notifyItemInserted(moduleList.size());
                                            loadingDialog.dismiss();

                                        }
                                    });

                            batch.update(lessonDoc,"NO_OF_MODULES",ctrModule);
                            batch.commit();


                        }
                    });
                }
            }});
    }


    private void loadModules() {

        moduleList.clear();

        loadingDialog.show();

        firestore.collection("Lessons").document(LessonFragment.lessonList.get(LessonFragment.selected_lesson_index)
                .getLe_id()).collection("MODULE_LIST").document("MODULE_INFO")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        loadingDialog.dismiss();

                        int noOfModules = LessonFragment.lessonList.get(LessonFragment.selected_lesson_index).getNoOfModules();

                        for(int i = 1; i <= noOfModules; i++)
                        {


                            moduleList.add(new ModuleModel(
                                    documentSnapshot.getString("MODULE"+ String.valueOf(i) + "_ID"),
                                    documentSnapshot.getString("MODULE"+ String.valueOf(i) + "_PDF")
                            ));

                        }

                        adapter = new ModuleAdapter(moduleList);
                        moduleView.setAdapter(adapter);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismiss();
                        Toast.makeText(ModuleActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();

                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            ModuleActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}