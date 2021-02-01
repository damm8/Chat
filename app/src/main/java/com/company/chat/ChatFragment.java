package com.company.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.chat.databinding.FragmentChatBinding;
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
    List<Mensaje> mensajes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentChatBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDb = FirebaseFirestore.getInstance();

        binding.enviar.setOnClickListener(v -> {
            String mensaje = binding.mensaje.getText().toString();
            String autor = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String fecha = LocalDateTime.now().toString();

            mDb.collection("mensajes")
                    .add(new Mensaje(mensaje, autor, fecha));
        });


        mDb.collection("mensajes").addSnapshotListener((value, error) -> {
            List<Mensaje> mensajes = new ArrayList<>();

            for(QueryDocumentSnapshot d: value){
                mensajes.add(new Mensaje(d.getString("mensaje"), d.getString("autor"), d.getString("fecha")));
            }
        });
    }


}