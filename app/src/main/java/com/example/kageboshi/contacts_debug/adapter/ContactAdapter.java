package com.example.kageboshi.contacts_debug.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kageboshi.contacts_debug.R;
import com.example.kageboshi.contacts_debug.http.model.ContactResponseModel;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Context context;
    private List<ContactResponseModel.DataBean.ContactsBean> contactList;
    private final LayoutInflater inflater;


    public ContactAdapter(Context context, List<ContactResponseModel.DataBean.ContactsBean> contactList) {
        this.contactList = contactList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_list_contact, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.text_name.setText(contactList.get(i).getName());
        viewHolder.text_phone.setText(contactList.get(i).getPhone());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView text_phone;
        private final TextView text_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_phone = ((TextView) itemView.findViewById(R.id.tel_list_contact));
            text_name = ((TextView) itemView.findViewById(R.id.name_list_contact));
        }
    }
}
