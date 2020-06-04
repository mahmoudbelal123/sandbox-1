/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.payment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityAddCardBinding;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.CardRequest;
import com.appster.turtle.network.response.PostCardResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import static com.stripe.android.model.Card.BRAND_RESOURCE_MAP;
/*
* Activity for add card
 */
public class AddCardActivity extends BaseActivity {

    private ActivityAddCardBinding mBinding;
    private Card card;
    private int expMonth = 0, expYear = 0;
    private RetrofitRestRepository mRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_card);
        mRepository = ((ApplicationController) getApplication()).provideRepository();
        initUi();
    }

    @Override
    public String getActivityName() {
        return AddCardActivity.class.getName();
    }

    private void initUi() {

        setUpHeader(mBinding.header.clHeader, "Add Card", R.drawable.ic_back_half_arrow, 0);

        mBinding.tvSaveBtn.setOnClickListener(view -> addCardToStripe());
        mBinding.etCardNumber.addTextChangedListener(cardNumberWatcher);
        mBinding.etCardExpiry.addTextChangedListener(commonWatcher);
        mBinding.etCardName.addTextChangedListener(commonWatcher);
        mBinding.etCardCvc.addTextChangedListener(commonWatcher);
        mBinding.etCardNumber.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(24),
                new CardNumberInputFilter(),

        });
        mBinding.etCardNumber.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mBinding.etCardExpiry.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mBinding.etCardExpiry.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(5),
                new CardExpiryInputFilter(),
        });
        mBinding.etCardCvc.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                hideKeyboard();
                return true;
            }
            return false;
        });

    }

    private void addCardToStripe() {

        if (!validateInputs())
            return;


        card = new Card(mBinding.etCardNumber.getText().toString(),
                expMonth,
                expYear,
                mBinding.etCardCvc.getText().toString(),
                mBinding.etCardName.getText().toString(),
                null,
                null,
                null,
                null,
                null,
                null,
                null

        );

        if (card.validateCard()) {

            showProgressBar();

            Stripe stripe = new Stripe(AddCardActivity.this, Constants.STRIPE);

            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your server
                            addCardApi(token.getId());
                        }

                        public void onError(Exception error) {

                            hideProgressBar();
                        }
                    }
            );
        } else {
            StringUtils.displaySnackBar(mBinding.etCardName, getString(R.string.card_detail_validation_err));

        }
    }

    private void addCardApi(String token) {


        new BaseCallback<PostCardResponse>(this, mRepository.getApiService().addCard(new CardRequest(token)), false) {

            @Override
            public void onSuccess(PostCardResponse response) {
                hideProgressBar();


                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.CARD, response.getData());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFail() {

                hideProgressBar();
            }
        };
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean validateInputs() {
        if (mBinding.etCardNumber.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.etCardName, getString(R.string.card_num_empty_err));
            return false;
        }
        if (mBinding.etCardCvc.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.etCardName, getString(R.string.card_cvc_err));
            return false;

        }
        if (mBinding.etCardName.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.etCardName, getString(R.string.card_name_err));
            return false;

        }
        if (mBinding.etCardExpiry.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.etCardName, getString(R.string.card_expiry_err));
            return false;

        }

        if (mBinding.etCardExpiry.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.etCardName, getString(R.string.card_expiry_err));
            return false;

        }

        if (mBinding.etCardExpiry.getText().toString().contains("/")) {
            String[] exp = mBinding.etCardExpiry.getText().toString().split("/");

            if (exp.length != 2) {
                StringUtils.displaySnackBar(mBinding.etCardName, getString(R.string.card_expiry_err));
                return false;
            }

            expMonth = Integer.parseInt(exp[0]);
            expYear = Integer.parseInt(exp[1]);

            if (expMonth > 12) {
                StringUtils.displaySnackBar(mBinding.etCardName, getString(R.string.card_expiry_err));
                return false;
            }

            if (!Utils.compareYear(Utils.getYear(String.valueOf(expYear)))) {
                StringUtils.displaySnackBar(mBinding.etCardName, getString(R.string.card_expiry_err));
                return false;
            }
        }


        return true;
    }

    private TextWatcher cardNumberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            if (mBinding.etCardNumber.getText().toString().isEmpty()) {
                mBinding.tvCardNumber.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.unselected_grey));
                return;
            } else {
                mBinding.tvCardNumber.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.black));
            }
            updateSaveBtn();

            card = new Card(mBinding.etCardNumber.getText().toString(), 0, 0, "");

            int res = BRAND_RESOURCE_MAP.get(card.getBrand());
            mBinding.etCardNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
        }

        @Override
        public void afterTextChanged(Editable editable) {

//
        }
    };

    private TextWatcher commonWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mBinding.etCardCvc.getText().toString().isEmpty()) {
                mBinding.tvCardCvc.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.unselected_grey));
            } else {
                mBinding.tvCardCvc.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.black));
            }


            if (mBinding.etCardName.getText().toString().isEmpty()) {
                mBinding.tvCardName.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.unselected_grey));
            } else {
                mBinding.tvCardName.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.black));
            }


            if (mBinding.etCardExpiry.getText().toString().isEmpty()) {
                mBinding.tvCardExpiry.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.unselected_grey));
            } else {
                mBinding.tvCardExpiry.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.black));
            }
            updateSaveBtn();
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    private void updateSaveBtn() {
        if (!mBinding.etCardNumber.getText().toString().isEmpty() &&
                !mBinding.etCardName.getText().toString().isEmpty() &&
                !mBinding.etCardExpiry.getText().toString().isEmpty() &&
                !mBinding.etCardCvc.getText().toString().isEmpty()) {
            mBinding.tvSaveBtn.setBackgroundResource(R.drawable.circle_yellow_button);
            mBinding.tvSaveBtn.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.app_white));
        } else {
            mBinding.tvSaveBtn.setBackgroundResource(R.drawable.gray_hollow_round_rect);
            mBinding.tvSaveBtn.setTextColor(ContextCompat.getColor(AddCardActivity.this, R.color.edit_text_footer_grey));


        }
    }

    private class CardNumberInputFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (dest != null && dest.toString().trim().length() > 23) return null;
            if (source.length() == 1 && ((dstart + 1) % 5 == 0))
                return "-" + source.toString();
            return null; // keep original
        }
    }

    private class CardExpiryInputFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (dest != null && dest.toString().trim().length() > 5) return null;
            if (source.length() == 1 && (dstart == 2))
                return "/" + source.toString();
            return null; // keep original
        }
    }


}
