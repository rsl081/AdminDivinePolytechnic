package com.s2dioapps.admindivinepolytechnic.ui.lesson;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.ArrayMap;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s2dioapps.admindivinepolytechnic.R;
import com.s2dioapps.admindivinepolytechnic.ui.module.ModuleActivity;
import com.s2dioapps.admindivinepolytechnic.ui.subject.SubjectFragment;
import com.s2dioapps.admindivinepolytechnic.ui.test.TestActivity;

import java.util.List;
import java.util.Map;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {

    private List<LessonModel> lesson_list;

    public LessonAdapter(List<LessonModel> lesson_list) {
        this.lesson_list = lesson_list;
    }

    @NonNull
    @Override
    public LessonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_item_layout,viewGroup,false);

        return new LessonAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull LessonAdapter.ViewHolder holder, int position) {

        String title = lesson_list.get(position).getNameModules();

        holder.setData(title, position, this);

    }
    public int getItemCount() {
        return lesson_list.size();
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

        private void setData(String title, final int pos, final LessonAdapter adapter)
        {
            lessonName.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LessonFragment.selected_lesson_index = pos;
                    Intent intent = new Intent(itemView.getContext(), ModuleActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });


            deleteB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Category")
                            .setMessage("Do you want to delete this category ?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteLesson(pos, itemView.getContext(), adapter);
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

//                    dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.GRAY);
//                    dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.GRAY);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,50,0);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);

                }
            });

        }//end of setData

        private void deleteLesson(final int id, final Context context, final LessonAdapter adapter) {
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String,Object> lessonDoc = new ArrayMap<>();
            int index=1;

            for(int i=0; i < lesson_list.size(); i++)
            {
                if( i != id)
                {
                    lessonDoc.put("SUB" + String.valueOf(index) + "_ID", lesson_list.get(i).getLe_id());
                    lessonDoc.put("SUB" + String.valueOf(index) + "_NAME", lesson_list.get(i).getNameModules());
                    lessonDoc.put("SUB" + String.valueOf(index) + "_NO_OF_MODULES", lesson_list.get(i).getNoOfModules());
                    index++;
                }else{

                    firestore.collection("Lessons").document(lesson_list.get(i).getLe_id())
                            .delete();

                }

            }

            lessonDoc.put("COUNT", index - 1);

            firestore.collection("Lessons").document("SUBJECTS")
                    .set(lessonDoc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(context,"Lessons deleted successfully",Toast.LENGTH_SHORT).show();

                            LessonFragment.lessonList.remove(id);

                            adapter.notifyDataSetChanged();

                            loadingDialog.dismiss();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    });


        }//end of deleteLesson


    }


}
