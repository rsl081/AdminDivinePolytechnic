package com.s2dioapps.admindivinepolytechnic.ui.subject;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.s2dioapps.admindivinepolytechnic.R;
import com.s2dioapps.admindivinepolytechnic.ui.test.TestActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SubjectFragment extends Fragment {

    private RecyclerView cat_recycler_view;
    private Button addCatB;
    public static List<SubjectModel> catList = new ArrayList<>();
    public static int selected_cat_index=0;

    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addCatDialog;
    private EditText dialogCatName;
    private Button dialogAddB;
    private SubjectAdapter adapter;


    public SubjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subject, container, false);

        cat_recycler_view = view.findViewById(R.id.cat_recycler);
        addCatB = view.findViewById(R.id.addCatB);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addCatDialog = new Dialog(getContext());
        addCatDialog.setContentView(R.layout.add_sub_dialog);
        addCatDialog.setCancelable(true);
        addCatDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogCatName = addCatDialog.findViewById(R.id.ac_cat_name);
        dialogAddB = addCatDialog.findViewById(R.id.ac_add_btn);

        firestore = FirebaseFirestore.getInstance();

        addCatB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCatName.getText().clear();
                addCatDialog.show();
            }
        });

        dialogAddB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogCatName.getText().toString().isEmpty())
                {
                    dialogCatName.setError("Enter Subject Name");
                    return;
                }

                addNewCategory(dialogCatName.getText().toString());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cat_recycler_view.setLayoutManager(layoutManager);

        loadData();

        return view;
    }

    void addNewCategory(final String title)
    {

        addCatDialog.dismiss();
        loadingDialog.show();

        WriteBatch batch = firestore.batch();

        final String doc_id = firestore.collection("Quiz").document().getId();

        final Map<String,Object> catData = new ArrayMap<>();
        catData.put("CAT_ID",doc_id);
        catData.put("NAME",title);
        catData.put("NO_OF_TESTS",0);
        catData.put("COUNTER","1");

        DocumentReference testDoc = firestore.collection("Quiz")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("TEST_LIST")
                .document("TEST_INFO");

        batch.set(testDoc, catData, SetOptions.merge());
        batch.commit();


        firestore.collection("Quiz").document(doc_id)
                .set(catData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void unused) {

                        Map<String,Object> catDoc = new ArrayMap<>();
                        catDoc.put("CAT" + String.valueOf(catList.size() + 1) + "_ID",doc_id);
                        catDoc.put("CAT" + String.valueOf(catList.size() + 1) + "_NAME",title);
                        catDoc.put("CAT" + String.valueOf(catList.size() + 1) + "_NO_OF_TESTS",0);
                        catDoc.put("COUNT", catList.size() + 1);

                        firestore.collection("Quiz").document("Categories")
                                .update(catDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getContext(),"Subject added successfully",Toast.LENGTH_SHORT).show();

                                        catList.add(new SubjectModel(doc_id,title,0,"1"));

                                        adapter.notifyItemInserted(catList.size());

                                        loadingDialog.dismiss();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });



    }

    private void loadData()
    {
        loadingDialog.show();

        catList.clear();

        firestore.collection("Quiz").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())
                    {
                        long count = (long)doc.get("COUNT");

                        for(int i=1; i <= count; i++)
                        {
                            String catName = doc.getString("CAT" + String.valueOf(i) + "_NAME");
                            String catid = doc.getString("CAT" + String.valueOf(i) + "_ID");
                            int noOfTest = doc.getLong("CAT" + String.valueOf(i) + "_NO_OF_TESTS").intValue();

                            catList.add(new SubjectModel(catid,catName,noOfTest,"1"));
                        }

                        adapter = new SubjectAdapter(catList);
                        cat_recycler_view.setAdapter(adapter);

                    }
                    else
                    {
                        Toast.makeText(getContext(),"No Category Document Exists!",Toast.LENGTH_SHORT).show();
                        //finish();
                    }

                }
                else
                {

                    Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();
            }

        });

    }

}