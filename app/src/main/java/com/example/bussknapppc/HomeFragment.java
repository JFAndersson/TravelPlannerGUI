package com.example.bussknapppc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.bussknapppc.storageframework.GetStoredInformation;
import com.example.bussknapppc.storageframework.TicketsFragmentStorage;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //TODO: Skapa en tinderliknande shuffle-funktion för biljettkorten så att flera biljetter kan skrollas igenom

        CardView noTicketView = view.findViewById(R.id.noTicketView);
        CardView ticketCard = view.findViewById(R.id.ticketCard);
        CardView stopButton = view.findViewById(R.id.stoppKnapp);
        TextView noTicketText = view.findViewById(R.id.noTicketText);
        TextView numTicket = view.findViewById(R.id.textTicketNum);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetStoredInformation.callStop("JFAndersson", 1);
            }
        });

        if (TicketsFragmentStorage.ticketCount.size() > 0){
            noTicketView.setVisibility(View.GONE);
            noTicketText.setVisibility(View.GONE);
            ticketCard.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            numTicket.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
