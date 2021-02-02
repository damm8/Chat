package com.company.chat;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.company.chat.databinding.FragmentSignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;


public class SignUpFragment extends Fragment {

    static public class SignUpViewModel extends AndroidViewModel {

        public SignUpViewModel(@NonNull Application application) {
            super(application);
        }

        Uri photoUri;
    }

    private FragmentSignUpBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private NavController mNav;
    private SignUpViewModel mVM;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentSignUpBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mNav = Navigation.findNavController(view);
        mVM = new ViewModelProvider(this).get(SignUpViewModel.class);

        binding.foto.setOnClickListener(v -> {
            galeria.launch("image/*");
        });

        binding.emailSignUp.setOnClickListener(v ->{
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            String name = binding.name.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mStorage.getReference("avatars/"+ UUID.randomUUID())
                                .putFile(mVM.photoUri)
                                .continueWithTask(task2 -> task2.getResult().getStorage().getDownloadUrl())
                                .addOnSuccessListener(url ->mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(url).build()));

                        mNav.navigate(R.id.action_signUpFragment_to_chatFragment);
                    } else {
                        Log.e("ABCD", task.getResult().toString());
                    }
                });
        });

        if (mVM.photoUri != null) Glide.with(this).load(mVM.photoUri).into(binding.foto);
    }

    private final ActivityResultLauncher<String> galeria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        mVM.photoUri = uri;
        Glide.with(this).load(uri).into(binding.foto);
    });
}