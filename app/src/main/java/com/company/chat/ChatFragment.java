package com.company.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.company.chat.databinding.FragmentChatBinding;
import com.company.chat.databinding.ViewholderChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private FirebaseFirestore mDb;
    private String email;
    private List<Mensaje> mensajes = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentChatBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDb = FirebaseFirestore.getInstance();
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        binding.enviar.setOnClickListener(v -> {
            String mensaje = binding.mensaje.getText().toString();
            String autor = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String fecha = LocalDateTime.now().toString();

            mDb.collection("mensajes")
                    .add(new Mensaje(mensaje, autor, fecha));
        });

        ChatAdapter chatAdapter = new ChatAdapter();
        binding.recyclerView.setAdapter(chatAdapter);

        mDb.collection("mensajes").addSnapshotListener((value, error) -> {
            mensajes = new ArrayList<>();

            for(QueryDocumentSnapshot d: value){
                mensajes.add(new Mensaje(d.getString("mensaje"), d.getString("autor"), d.getString("fecha")));
            }
            chatAdapter.notifyDataSetChanged();
        });


    }

    class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.e("ABCD","creando un viewholder");
            return new ChatViewHolder(ViewholderChatBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            Log.e("ABCD","Rellendando el viewholder " + position);
            Mensaje mensaje = mensajes.get(position);

            if(mensaje.autor.equals(email)){
                holder.binding.todo.setGravity(Gravity.END);
            }
            holder.binding.autor.setText(mensaje.autor);
            holder.binding.mensaje.setText(mensaje.mensaje);
            holder.binding.fecha.setText(mensaje.fecha);
        }

        @Override
        public int getItemCount() {
            Log.e("ABCD", "Nuemros chats " + mensajes.size());
            return mensajes.size();
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder{
        ViewholderChatBinding binding;
        public ChatViewHolder(@NonNull ViewholderChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}