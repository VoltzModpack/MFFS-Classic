package mffs.api;

import mffs.api.security.IBiometricIdentifier;

import java.util.Set;

public interface IBiometricIdentifierLink {

	IBiometricIdentifier getBiometricIdentifier();

	Set getBiometricIdentifiers();
}
