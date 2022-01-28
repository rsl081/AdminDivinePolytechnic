package com.s2dioapps.admindivinepolytechnic.ui.module;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.s2dioapps.admindivinepolytechnic.R;
import com.s2dioapps.admindivinepolytechnic.ui.lesson.LessonFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> {

    private List<ModuleModel> module_list;

    public ModuleAdapter(List<ModuleModel> module_list) {
        this.module_list = module_list;
    }


    @NonNull
    @Override
    public ModuleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.sub_item_layout,viewGroup,false);

        return new ModuleAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ModuleAdapter.ViewHolder holder, int position) {

        String title = module_list.get(position).getModuleId();

        holder.moduleData(title, position, this);

    }

    public int getItemCount() {
        return module_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView lessonName;
        private ImageView deleteB;
        private Dialog loadingDialog;
        private Dialog editDialog;
        private EditText tv_editLessonName;
        private Button updateLessonB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lessonName = itemView.findViewById(R.id.catName);
            deleteB = itemView.findViewById(R.id.catDelB);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            editDialog = new Dialog(itemView.getContext());
            editDialog.setContentView(R.layout.edit_sub_dialog);
            editDialog.setCancelable(true);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


            tv_editLessonName = editDialog.findViewById(R.id.ec_cat_name);
            updateLessonB = editDialog.findViewById(R.id.ec_add_btn);


        }
        private void moduleData(String title, final int pos, final ModuleAdapter adapter)
        {
            lessonName.setText(title);

            deleteB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Module")
                            .setMessage("Do you want to delete this module ?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteModule(pos, itemView.getContext(), adapter);
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

//                    dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
//                    dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.RED);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,50,0);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);

                }
            });


        }//end of moduleData

        private void deleteModule(final int pos, final Context context, final ModuleAdapter adapter) {

            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            DocumentReference docRef = firestore.collection("Lessons")
                    .document(LessonFragment.lessonList.get(LessonFragment.selected_lesson_index).getLe_id())
                    .collection("MODULE_LIST")
                    .document("MODULE_INFO");

            final Map<String,Object> updates = new HashMap<>();
            updates.put("MODULE"+ (pos + 1) +"_ID", FieldValue.delete());
            updates.put("MODULE"+ (pos + 1)+"_PDF", FieldValue.delete());

            docRef.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {


                            ModuleActivity.moduleList.remove(pos);
                            adapter.notifyDataSetChanged();

                            DocumentReference setDocRef = firestore.collection("Lessons")
                                    .document(LessonFragment.lessonList.get(LessonFragment.selected_lesson_index).getLe_id())
                                    .collection("MODULE_LIST")
                                    .document("MODULE_INFO");

                            final Map<String,Object> setUpdates = new HashMap<>();



                            for(int i = 0; i < ModuleActivity.moduleList.size(); i++)
                            {
                                setUpdates.put("MODULE"+ (i + 1) +"_ID", ModuleActivity.moduleList.get(i).getModuleId());
                                setUpdates.put("MODULE"+ (i + 1 ) +"_PDF", ModuleActivity.moduleList.get(i).getModulePdf());

                            }
                            setDocRef.set(setUpdates);


                        }
                    });



            DocumentReference docTest = firestore.collection("Lessons")
                    .document(LessonFragment.lessonList.get(LessonFragment.selected_lesson_index).getLe_id());

            Map<String,Object> test = new HashMap<>();
            test.put("NO_OF_MODULES", --ModuleActivity.ctrModule);
            docTest.update(test)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            LessonFragment.lessonList.get(LessonFragment.selected_lesson_index).setNoOfModules(ModuleActivity.ctrModule);

                            DocumentReference docCategories = firestore.collection("Lessons")
                                    .document("SUBJECTS");

                            Map<String,Object> categories = new HashMap<>();
                            categories.put("SUB"+ (LessonFragment.selected_lesson_index + 1) +"_NO_OF_MODULES", ModuleActivity.ctrModule);
                            docCategories.update(categories);

                            loadingDialog.dismiss();
                        }
                    });
        }//end of deleteModule
    }



}
