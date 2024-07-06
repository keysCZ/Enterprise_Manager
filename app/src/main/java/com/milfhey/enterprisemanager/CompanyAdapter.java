package com.milfhey.enterprisemanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {
    private List<Company> companyList;
    private Context context;

    public CompanyAdapter(List<Company> companyList, Context context) {
        this.companyList = companyList;
        this.context = context;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        Company company = companyList.get(position);
        holder.nameTextView.setText(company.getName());
        holder.addressTextView.setText(company.getAddress());
        holder.phoneTextView.setText(company.getPhone());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CompanyDetailActivity.class);
            intent.putExtra("companyId", company.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    public void updateCompanies(List<Company> companies) {
        this.companyList = companies;
        notifyDataSetChanged();
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, phoneTextView;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
        }
    }
}
