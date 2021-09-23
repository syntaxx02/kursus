package com.cahyaa.chatapp.presentation.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.cahyaa.chatapp.R;

public class OnboardingAdapter extends PagerAdapter {

    private Context context;
    private OnboardingListener onboardingListener;

    private int[] layouts = {
            R.layout.layout_onboarding_1,
            R.layout.layout_onboarding_2,
            R.layout.layout_onboarding_3,
            R.layout.layout_onboarding_4
    };

    OnboardingAdapter(Context context, OnboardingListener onboardingListener) {
        this.context = context;
        this.onboardingListener = onboardingListener;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layouts[position], container, false);
        view.setTag(position);

        //Jika berada di onboarding page 1 s.d. 3 (position 0 s.d. 2 karena menggunakan index array)
        if (position < 3) {
            TextView onboardingTextViewSkip = view.findViewById(R.id.onboarding_textView_skip);

            onboardingTextViewSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onboardingListener.onSkipClickListener();
                }
            });
            //Jika berada di onboarding page 4
        } else {
            Button onboardingButtonLanjut = view.findViewById(R.id.onboarding_button_lanjut);

            onboardingButtonLanjut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onboardingListener.onContinueClickListener();
                }
            });
        }
        container.addView(view);

        return view;
    }

    //Stabilitas saat swipe antar halaman
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }

}

//Menghubungkan ke listener berisi intent yang menuju ke SignUpActivity
interface OnboardingListener {
    void onContinueClickListener();

    void onSkipClickListener();
}