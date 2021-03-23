package com.yxkj.facexradix.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.yxkj.facexradix.R;
import com.yxkj.facexradix.ui.activity.MainActivity;

public class PopupKeyboard extends PopupWindow implements View.OnClickListener {
     private final EditText editText;
     private ImageButton sw1;
     private ImageButton sw2;
     private ImageButton delete;
     private Button done;
     private Button Punctuation;
     private Button key1;
     private Button key2;
     private Button key3;
     private Button key4;
     private Button key5;
     private Button key6;
     private Button key7;
     private Button key8;
     private Button key9;
     private Button key10;
     private Button key11;
     private Button key12;
     private Button key13;
     private Button key14;
     private Button key15;
     private Button key16;
     private Button key17;
     private Button key18;
     private Button key19;
     private Button key20;
     private Button key21;
     private Button key22;
     private Button key23;
     private Button key24;
     private Button key25;
     private Button key26;
     private Button key27;
     private Button key28;
     private Button key29;
     private Button key30;
     private Button key31;
     private Button key32;

     private boolean isShift = false;
     private boolean isPunctuation = false;


     private StringBuilder sb;
     private final View contentView;

     public PopupKeyboard(Context context, EditText editText) {
        super(context);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
         contentView = LayoutInflater.from(context).inflate(R.layout.keyboard_view,
                 null, false);
         this.editText = editText;
         setContentView(contentView);
        findview();
         Editable text = editText.getText();
         sb = new StringBuilder(text);
     }

     public void switchKey() {
         if(!isShift) {
             key1.setText("Q");
             key2.setText("W");
             key3.setText("E");
             key4.setText("R");
             key5.setText("T");
             key6.setText("Y");
             key7.setText("U");
             key8.setText("I");
             key9.setText("O");
             key10.setText("P");
             key11.setText("A");
             key12.setText("S");
             key13.setText("D");
             key14.setText("F");
             key15.setText("G");
             key16.setText("H");
             key17.setText("J");
             key18.setText("K");
             key19.setText("L");
             key20.setText("Z");
             key21.setText("X");
             key22.setText("C");
             key23.setText("V");
             key24.setText("B");
             key25.setText("N");
             key26.setText("M");
             isShift = true;
         }else{
             key1.setText("q");
             key2.setText("w");
             key3.setText("e");
             key4.setText("r");
             key5.setText("t");
             key6.setText("y");
             key7.setText("u");
             key8.setText("i");
             key9.setText("o");
             key10.setText("p");
             key11.setText("a");
             key12.setText("s");
             key13.setText("d");
             key14.setText("f");
             key15.setText("g");
             key16.setText("h");
             key17.setText("j");
             key18.setText("k");
             key19.setText("l");
             key20.setText("z");
             key21.setText("x");
             key22.setText("c");
             key23.setText("v");
             key24.setText("b");
             key25.setText("n");
             key26.setText("m");
             isShift = false;
         }
         isPunctuation = false;
     }

     public void punctuation() {
         if (!isPunctuation) {
             key1.setText("1");
             key2.setText("2");
             key3.setText("3");
             key4.setText("4");
             key5.setText("5");
             key6.setText("6");
             key7.setText("7");
             key8.setText("8");
             key9.setText("9");
             key10.setText("0");
             key11.setText("@");
             key12.setText("#");
             key13.setText("$");
             key14.setText("%");
             key15.setText("&");
             key16.setText("-");
             key17.setText("+");
             key18.setText("(");
             key19.setText(")");
             key20.setText("\\");
             key21.setText("=");
             key22.setText("*");
             key23.setText("\"");
             key24.setText("'");
             key25.setText(":");
             key26.setText(";");
             isPunctuation = true;
         }else{
             key1.setText("q");
             key2.setText("w");
             key3.setText("e");
             key4.setText("r");
             key5.setText("t");
             key6.setText("y");
             key7.setText("u");
             key8.setText("i");
             key9.setText("o");
             key10.setText("p");
             key11.setText("a");
             key12.setText("s");
             key13.setText("d");
             key14.setText("f");
             key15.setText("g");
             key16.setText("h");
             key17.setText("j");
             key18.setText("k");
             key19.setText("l");
             key20.setText("z");
             key21.setText("x");
             key22.setText("c");
             key23.setText("v");
             key24.setText("b");
             key25.setText("n");
             key26.setText("m");
             isPunctuation = false;
         }
         isShift = false;
     }

     public void delete() {
         deleteTip();
     }

     public void done() {
//         keyboardListener.onDone();
     }


     private void findview() {
         sw1 = contentView.findViewById(R.id.sw1);
         sw1.setOnClickListener(this);
         sw2 = contentView.findViewById(R.id.sw2);
         sw2.setOnClickListener(this);
         delete = contentView.findViewById(R.id.delete);
         delete.setOnClickListener(this);
         delete.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {
                 sb.delete(0, sb.length());
                 editText.setText(sb.toString());
                 return true;
             }
         });
         done = contentView.findViewById(R.id.done);
         done.setOnClickListener(this);
         Punctuation = contentView.findViewById(R.id.Punctuation);
         Punctuation.setOnClickListener(this);
         key1 = contentView.findViewById(R.id.key1);
         key1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key1.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key1.getText().toString());
             }

         });
         key2 = contentView.findViewById(R.id.key2);
         key2.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key2.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key2.getText().toString());
             }

         });
         key3 = contentView.findViewById(R.id.key3);
         key3.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key3.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key3.getText().toString());
             }

         });
         key4 = contentView.findViewById(R.id.key4);
         key4.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key4.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key4.getText().toString());
             }

         });
         key5 = contentView.findViewById(R.id.key5);
         key5.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key5.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key5.getText().toString());
             }

         });
         key6 = contentView.findViewById(R.id.key6);
         key6.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key6.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key6.getText().toString());
             }

         });
         key7 = contentView.findViewById(R.id.key7);
         key7.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key7.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key7.getText().toString());
             }

         });
         key8 = contentView.findViewById(R.id.key8);
         key8.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key8.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key8.getText().toString());
             }

         });
         key9 = contentView.findViewById(R.id.key9);
         key9.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key9.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key9.getText().toString());
             }

         });
         key10 = contentView.findViewById(R.id.key10);
         key10.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key10.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key10.getText().toString());
             }

         });
         key11 = contentView.findViewById(R.id.key11);
         key11.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key11.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key11.getText().toString());
             }

         });
         key12 = contentView.findViewById(R.id.key12);
         key12.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key12.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key12.getText().toString());
             }

         });
         key13 = contentView.findViewById(R.id.key13);
         key13.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key13.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key13.getText().toString());
             }

         });
         key14 = contentView.findViewById(R.id.key14);
         key14.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key14.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key14.getText().toString());
             }

         });
         key15 = contentView.findViewById(R.id.key15);
         key15.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key15.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key15.getText().toString());
             }

         });
         key16 = contentView.findViewById(R.id.key16);
         key16.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key16.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key16.getText().toString());
             }

         });
         key17 = contentView.findViewById(R.id.key17);
         key17.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key17.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key17.getText().toString());
             }

         });
         key18 = contentView.findViewById(R.id.key18);
         key18.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key18.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key18.getText().toString());
             }

         });
         key19 = contentView.findViewById(R.id.key19);
         key19.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key19.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key19.getText().toString());
             }

         });
         key20 = contentView.findViewById(R.id.key20);
         key20.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key20.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key20.getText().toString());
             }

         });
         key21 = contentView.findViewById(R.id.key21);
         key21.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key21.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key21.getText().toString());
             }

         });
         key22 = contentView.findViewById(R.id.key22);
         key22.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key22.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key22.getText().toString());
             }

         });
         key23 = contentView.findViewById(R.id.key23);
         key23.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key23.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key23.getText().toString());
             }

         });
         key24 = contentView.findViewById(R.id.key24);
         key24.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key24.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key24.getText().toString());
             }

         });
         key25 = contentView.findViewById(R.id.key25);
         key25.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key25.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key25.getText().toString());
             }

         });
         key26 = contentView.findViewById(R.id.key26);
         key26.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key26.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key26.getText().toString());
             }

         });
         key27 = contentView.findViewById(R.id.key27);
         key27.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key27.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key27.getText().toString());
             }

         });
         key28 = contentView.findViewById(R.id.key28);
         key28.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key28.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key28.getText().toString());
             }

         });
         key29 = contentView.findViewById(R.id.key29);
         key29.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key29.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key29.getText().toString());
             }

         });
         key30 = contentView.findViewById(R.id.key30);
         key30.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key30.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key30.getText().toString());
             }

         });
         key31 = contentView.findViewById(R.id.key31);
         key31.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key31.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key31.getText().toString());
             }

         });
         key32 = contentView.findViewById(R.id.key32);
         key32.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addTvTip(key32.getText());
                 MainActivity.resetOperateTime();
                 Log.e("Key", key32.getText().toString());
             }
         });
     }


     @Override
     public void onClick(View v) {
         switch (v.getId()) {
             case R.id.sw1:
                 switchKey();
                 MainActivity.resetOperateTime();
                 break;
             case R.id.sw2:
                 switchKey();
                 MainActivity.resetOperateTime();
                 break;
             case R.id.Punctuation:
                 punctuation();
                 MainActivity.resetOperateTime();
                 break;
             case R.id.delete:
                 delete();
                 MainActivity.resetOperateTime();
                 break;
             case R.id.done:
                 done();
                 MainActivity.resetOperateTime();
                 break;
         }
     }


     public void addTvTip(CharSequence tip) {
         sb.append(tip);
         editText.setText(sb.toString());
     }




     public void deleteTip() {
         if(sb.length() != 0) {
             sb.delete(sb.length() - 1, sb.length());
         }
         Editable text = editText.getText();
         if (text.length() - 1 >= 0) {
             Editable delete = text.delete(text.length() - 1, text.length());
             editText.setText(delete);
             editText.setSelection(delete.length());
         }

     }

}