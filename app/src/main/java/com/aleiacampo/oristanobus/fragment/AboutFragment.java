package com.aleiacampo.oristanobus.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.aleiacampo.oristanobus.R;

public class AboutFragment extends Fragment {

  View view;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_about, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onStart() {
        super.onStart();

        TextView textViewMail = (TextView) view.findViewById(R.id.textView_email);
        textViewMail.setText(Html.fromHtml("Trovato un bug? Contattaci su <u>oristanobus@gmail.com</u>"));
        TextView textViewLink = (TextView) view.findViewById(R.id.textView_link);
        textViewLink.setText(Html.fromHtml("Questa è un’app di consultazione, i dati presenti provengono dalle " +
              "tabelle pubbliche presenti al link: <u>http://www.arstspa.info/Oristano.html</u>, non vi " +
              "è pertanto responsabilità sugli orari dei pullman da parte degli sviluppatori."));
        textViewMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, "oristanobus@gmail.com");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client"));
            }
        });
        textViewLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("http://androidbook.blogspot.com/");
                Intent link = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(link);
            }
        });
  }
}
