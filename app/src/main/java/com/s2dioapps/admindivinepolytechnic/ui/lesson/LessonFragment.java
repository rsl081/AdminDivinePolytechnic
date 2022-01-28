package com.s2dioapps.admindivinepolytechnic.ui.lesson;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonFragment extends Fragment {

    private RecyclerView lesson_recycler_view;
    private Button addLessonB;
    public static List<LessonModel> lessonList = new ArrayList<>();
    public static int selected_lesson_index=0;

    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addLessonDialog;
    private EditText dialogCatName;
    private Button dialogAddB;
    private LessonAdapter adapter;

    public LessonFragment()
    {

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lesson, container, false);


        lesson_recycler_view = view.findViewById(R.id.lesson_recycler);
        addLessonB = view.findViewById(R.id.addLessonB);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addLessonDialog = new Dialog(getContext());
        addLessonDialog.setContentView(R.layout.add_sub_dialog);
        addLessonDialog.setCancelable(true);
        addLessonDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogCatName = addLessonDialog.findViewById(R.id.ac_cat_name);
        dialogAddB = addLessonDialog.findViewById(R.id.ac_add_btn);

        firestore = FirebaseFirestore.getInstance();

        addLessonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCatName.getText().clear();
                addLessonDialog.show();
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

                addNewLesson(dialogCatName.getText().toString());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lesson_recycler_view.setLayoutManager(layoutManager);

        loadData();

        return view;
    }
    void addNewLesson(final String title)
    {
        addLessonDialog.dismiss();
        loadingDialog.show();

        WriteBatch batch = firestore.batch();

        final String lesson_id = firestore.collection("Lessons").document().getId();

        final Map<String,Object> lessonData = new ArrayMap<>();
        lessonData.put("LE_ID",lesson_id);
        lessonData.put("NAME",title);
        lessonData.put("NO_OF_MODULES",0);

        DocumentReference lessonDoc = firestore.collection("Lessons")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("MODULE_LIST")
                .document("MODULE_INFO");


        batch.set(lessonDoc, lessonData, SetOptions.merge());
        batch.commit();

        final Map<String,Object> countData = new ArrayMap<>();
        countData.put("COUNT",1);

        firestore.collection("Lessons")
                .document(lesson_id)
                .collection("MODULE_LIST")
                .document("MODULE_INFO")
                .set(countData);

        firestore.collection("Lessons").document(lesson_id)
                .set(lessonData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {




                        Map<String,Object> lessonDoc = new ArrayMap<>();
                        lessonDoc.put("SUB" + String.valueOf(lessonList.size() + 1) + "_ID",lesson_id);
                        lessonDoc.put("SUB" + String.valueOf(lessonList.size() + 1) + "_NAME",title);
                        lessonDoc.put("SUB" + String.valueOf(lessonList.size() + 1) + "_NO_OF_MODULES",0);
                        lessonDoc.put("COUNT", lessonList.size() + 1);

                        firestore.collection("Lessons").document("SUBJECTS")
                                .update(lessonDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getContext(),"Lesson added successfully",Toast.LENGTH_SHORT).show();

                                        lessonList.add(new LessonModel(lesson_id,title,0));

                                        adapter.notifyItemInserted(lessonList.size());

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

        lessonList.clear();

        firestore.collection("Lessons").document("SUBJECTS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    String leId = doc.getString("SUB" + String.valueOf(i) + "_ID");
                                    String nameModules = doc.getString("SUB" + String.valueOf(i) + "_NAME");
                                    int noOfModules = doc.getLong("SUB" + String.valueOf(i) + "_NO_OF_MODULES").intValue();

                                    lessonList.add(new LessonModel(leId,nameModules,noOfModules));
                                }

                                adapter = new LessonAdapter(lessonList);
                                lesson_recycler_view.setAdapter(adapter);

                            } else {
                                Toast.makeText(getContext(),"No Lesson Document Exists!",Toast.LENGTH_SHORT).show();
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