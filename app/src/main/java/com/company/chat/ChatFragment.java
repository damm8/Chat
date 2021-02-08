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

import com.bumptech.glide.Glide;
import com.company.chat.databinding.FragmentChatBinding;
import com.company.chat.databinding.ViewholderChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private FirebaseFirestore mDb;
    private List<Mensaje> mensajes = new ArrayList<>();
    private FirebaseUser user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentChatBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDb = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        binding.enviar.setOnClickListener(v -> {
            String mensaje = binding.mensaje.getText().toString();
            String fecha = LocalDateTime.now().toString();

            mDb.collection("mensajes")
                    .add(new Mensaje(null, user.getDisplayName(), user.getPhotoUrl().toString(), mensaje, fecha));

            binding.mensaje.setText("");
        });

        ChatAdapter chatAdapter = new ChatAdapter();
        binding.recyclerView.setAdapter(chatAdapter);

        // collection ~~~ tabla
        // document ~~~ fila
        mDb.collection("mensajes")
                .orderBy("fecha")
                .addSnapshotListener((value, error) -> {
                    for (QueryDocumentSnapshot m: value){

                        String email = m.getString("autorEmail");
                        String nombre = m.getString("autorNombre");
                        String fecha = m.getString("fecha");
                        String texto = m.getString("mensaje");
                        String foto = m.getString("autorFoto");

                        Mensaje mensaje = new Mensaje(email, nombre, foto, texto, fecha);
                        mensajes.add(mensaje);
                    }
                    chatAdapter.notifyDataSetChanged();
                    binding.recyclerView.scrollToPosition(mensajes.size() - 1);
                });
    }

    class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChatViewHolder(ViewholderChatBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            Mensaje mensaje = mensajes.get(position);

            if(mensaje.autorEmail != null && mensaje.autorEmail.equals(user.getEmail())){
                Log.e("ABCD", "autor igual");
                holder.binding.todo.setGravity(Gravity.END);
            } else {
                Log.e("ABCD", "autor diferente");
                holder.binding.todo.setGravity(Gravity.START);
            }
            holder.binding.autor.setText(mensaje.autorNombre);
            holder.binding.mensaje.setText(mensaje.mensaje);
            holder.binding.fecha.setText(mensaje.fecha);
            Glide.with(requireView()).load(mensaje.autorFoto).into(holder.binding.foto);
        }

        @Override
        public int getItemCount() {
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
