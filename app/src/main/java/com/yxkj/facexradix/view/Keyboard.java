package com.yxkj.facexradix.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.yxkj.facexradix.R;

public class Keyboard extends LinearLayout implements View.OnClickListener {
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

    private final Context context;
    private View inflate;
    private KeyboardListener keyboardListener;
    private EditText editText;

    public Keyboard(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public Keyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        inflate = LayoutInflater.from(context).inflate(R.layout.keyboard_view, this);
        findview();
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
        keyboardListener.onDelete();
    }

    public void done() {
        keyboardListener.onDone();
    }


    private void findview() {
        sw1 = findViewById(R.id.sw1);
        sw1.setOnClickListener(this);
        sw2 = findViewById(R.id.sw2);
        sw2.setOnClickListener(this);
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(this);
        done = findViewById(R.id.done);
        done.setOnClickListener(this);
        Punctuation = findViewById(R.id.Punctuation);
        Punctuation.setOnClickListener(this);
        key1 = findViewById(R.id.key1);
        key1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key1.getText());
                Log.e("Key", key1.getText().toString());
            }

        });
        key2 = findViewById(R.id.key2);
        key2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key2.getText());
                Log.e("Key", key2.getText().toString());
            }

        });
        key3 = findViewById(R.id.key3);
        key3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key3.getText());
                Log.e("Key", key3.getText().toString());
            }

        });
        key4 = findViewById(R.id.key4);
        key4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key4.getText());
                Log.e("Key", key4.getText().toString());
            }

        });
        key5 = findViewById(R.id.key5);
        key5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key5.getText());
                Log.e("Key", key5.getText().toString());
            }

        });
        key6 = findViewById(R.id.key6);
        key6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key6.getText());
                Log.e("Key", key6.getText().toString());
            }

        });
        key7 = findViewById(R.id.key7);
        key7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key7.getText());
                Log.e("Key", key7.getText().toString());
            }

        });
        key8 = findViewById(R.id.key8);
        key8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key8.getText());
                Log.e("Key", key8.getText().toString());
            }

        });
        key9 = findViewById(R.id.key9);
        key9.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key9.getText());
                Log.e("Key", key9.getText().toString());
            }

        });
        key10 = findViewById(R.id.key10);
        key10.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key10.getText());
                Log.e("Key", key10.getText().toString());
            }

        });
        key11 = findViewById(R.id.key11);
        key11.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key11.getText());
                Log.e("Key", key11.getText().toString());
            }

        });
        key12 = findViewById(R.id.key12);
        key12.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key12.getText());
                Log.e("Key", key12.getText().toString());
            }

        });
        key13 = findViewById(R.id.key13);
        key13.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key13.getText());
                Log.e("Key", key13.getText().toString());
            }

        });
        key14 = findViewById(R.id.key14);
        key14.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key14.getText());
                Log.e("Key", key14.getText().toString());
            }

        });
        key15 = findViewById(R.id.key15);
        key15.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key15.getText());
                Log.e("Key", key15.getText().toString());
            }

        });
        key16 = findViewById(R.id.key16);
        key16.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key16.getText());
                Log.e("Key", key16.getText().toString());
            }

        });
        key17 = findViewById(R.id.key17);
        key17.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key17.getText());
                Log.e("Key", key17.getText().toString());
            }

        });
        key18 = findViewById(R.id.key18);
        key18.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key18.getText());
                Log.e("Key", key18.getText().toString());
            }

        });
        key19 = findViewById(R.id.key19);
        key19.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key19.getText());
                Log.e("Key", key19.getText().toString());
            }

        });
        key20 = findViewById(R.id.key20);
        key20.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key20.getText());
                Log.e("Key", key20.getText().toString());
            }

        });
        key21 = findViewById(R.id.key21);
        key21.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key21.getText());
                Log.e("Key", key21.getText().toString());
            }

        });
        key22 = findViewById(R.id.key22);
        key22.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key22.getText());
                Log.e("Key", key22.getText().toString());
            }

        });
        key23 = findViewById(R.id.key23);
        key23.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key23.getText());
                Log.e("Key", key23.getText().toString());
            }

        });
        key24 = findViewById(R.id.key24);
        key24.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key24.getText());
                Log.e("Key", key24.getText().toString());
            }

        });
        key25 = findViewById(R.id.key25);
        key25.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key25.getText());
                Log.e("Key", key25.getText().toString());
            }

        });
        key26 = findViewById(R.id.key26);
        key26.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key26.getText());
                Log.e("Key", key26.getText().toString());
            }

        });
        key27 = findViewById(R.id.key27);
        key27.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key27.getText());
                Log.e("Key", key27.getText().toString());
            }

        });
        key28 = findViewById(R.id.key28);
        key28.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key28.getText());
                Log.e("Key", key28.getText().toString());
            }

        });
        key29 = findViewById(R.id.key29);
        key29.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key29.getText());
                Log.e("Key", key29.getText().toString());
            }

        });
        key30 = findViewById(R.id.key30);
        key30.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key30.getText());
                Log.e("Key", key30.getText().toString());
            }

        });
        key31 = findViewById(R.id.key31);
        key31.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key31.getText());
                Log.e("Key", key31.getText().toString());
            }

        });
        key32 = findViewById(R.id.key32);
        key32.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardListener.onKey(key32.getText());
                Log.e("Key", key32.getText().toString());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sw1:
                switchKey();
                break;
            case R.id.sw2:
                switchKey();
                break;
            case R.id.Punctuation:
                punctuation();
                break;
            case R.id.delete:
                delete();
                break;
            case R.id.done:
                done();
                break;
        }
    }

    public void setOnkeyboardListener(KeyboardListener keyboardListener) {
        this.keyboardListener = keyboardListener;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }
}
