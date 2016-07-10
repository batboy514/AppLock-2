package com.tiba247.locker.ui;

import com.tiba247.locker.lock.LockPreferences;
import com.tiba247.locker.lock.LockService;
import com.tiba247.locker.util.PrefUtils;
import com.tiba247.util.DialogSequencer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.tiba247.locker.R;

class Dialogs {

	/**
	 * The dialog that allows the user to select between password and pattern
	 * options
	 * 
	 * @param c
	 * @return
	 */
	public static AlertDialog getChangePasswordDialog(final Context c) {
		final AlertDialog.Builder choose = new AlertDialog.Builder(c);
		choose.setTitle(R.string.old_main_choose_lock_type);
		choose.setItems(R.array.lock_type_names, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int type = which == 0 ? LockPreferences.TYPE_PASSWORD
						: LockPreferences.TYPE_PATTERN;
				LockService.showCreate(c, type);
			}
		});
		return choose.create();
	}

	// private void showVersionDialogs() {
	// if (mVersionManager.isDeprecated()) {
	// new VersionUtils(this).getDeprecatedDialog().show();
	// } else if (mVersionManager.shouldWarn()) {
	// new VersionUtils(this).getUpdateAvailableDialog().show();
	// }
	// }

	/**
	 * 
	 * @param c
	 * @param ds
	 * @return True if the dialog was added
	 */
	public static boolean addEmptyPasswordDialog(Context c,
			final DialogSequencer ds) {
		final boolean empty = new PrefUtils(c).isCurrentPasswordEmpty();
		if (empty) {
			ds.addDialog(getChangePasswordDialog(c));
			return true;
		}
		return false;
	}

	// private static AlertDialog getEmptyPasswordDialog(Context c,
	// final DialogSequencer ds) {
	//
	// final AlertDialog.Builder msg = new AlertDialog.Builder(c);
	// msg.setTitle(R.string.main_setup);
	// msg.setMessage(R.string.main_no_password);
	// msg.setCancelable(false);
	// msg.setPositiveButton(android.R.string.ok, null);
	// msg.setNegativeButton(android.R.string.cancel, new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// ds.removeNext(dialog);
	// }
	// });
	// return msg.create();
	// }

	public static AlertDialog getRecoveryCodeDialog(final Context c) {
		PrefUtils prefs = new PrefUtils(c);
		String code = prefs.getString(R.string.pref_key_recovery_code);
		if (code != null) {
			return null;
		}
		// Code = null
		code = PrefUtils.generateRecoveryCode(c);
		// save it directly to avoid it to change
		prefs.put(R.string.pref_key_recovery_code, code).apply();
		final String finalcode = code;
		AlertDialog.Builder ab = new AlertDialog.Builder(c);
		ab.setCancelable(false);
		ab.setNeutralButton(R.string.recovery_code_send_button,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(
								android.content.Intent.ACTION_SEND);
						i.setType("text/plain");
						i.putExtra(Intent.EXTRA_TEXT, c.getString(
								R.string.recovery_intent_message, finalcode));
						c.startActivity(Intent.createChooser(i,
								c.getString(R.string.recovery_intent_tit)));
					}
				});
		ab.setPositiveButton(android.R.string.ok, null);
		ab.setTitle(R.string.recovery_tit);
		ab.setMessage(String.format(c.getString(R.string.recovery_dlgmsg),
				finalcode));
		return ab.create();
	}
}
