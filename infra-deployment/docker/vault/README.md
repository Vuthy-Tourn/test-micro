# Vault Operations

This directory contains the persistent Vault deployment used by AuthGate.

## Current Setup

- Vault runs in server mode, not dev mode.
- Storage backend: local file storage at `/vault/file`
- Seal type: Shamir
- Unseal shares: 5
- Unseal threshold: 3
- Config server authenticates with AppRole, not a root token.
- Public UI/API is exposed through `https://vault.authgate.site`.
- Internal service-to-service traffic uses `https://vault:8200`.

## Sensitive Material

The following values must be stored outside this repository in a secure password manager or offline record:

- all 5 unseal keys
- the initial root token, if still retained
- config-server AppRole credentials
- Vault TLS private keys

Never commit unseal keys, root tokens, role IDs, secret IDs, or TLS private keys.

## Check Vault Status

```bash
docker exec vault sh -lc 'VAULT_ADDR=https://127.0.0.1:8200 VAULT_SKIP_VERIFY=true vault status'
```

Expected healthy output includes:

```text
Initialized     true
Sealed          false
Storage Type    file
Total Shares    5
Threshold       3
```

## Restart And Unseal Vault

Restart Vault:

```bash
docker restart vault
```

Check status:

```bash
docker exec vault sh -lc 'VAULT_ADDR=https://127.0.0.1:8200 VAULT_SKIP_VERIFY=true vault status'
```

After restart, `Sealed` should be `true`. This is expected.

Unseal Vault by running this command three times with three different base64 unseal keys:

```bash
docker exec -it vault sh -lc 'VAULT_ADDR=https://127.0.0.1:8200 VAULT_SKIP_VERIFY=true vault operator unseal'
```

Check status again:

```bash
docker exec vault sh -lc 'VAULT_ADDR=https://127.0.0.1:8200 VAULT_SKIP_VERIFY=true vault status'
```

After the third valid key, `Sealed` should be `false`.

## Verify Config Server Can Read Secrets

```bash
curl -s http://127.0.0.1:16904/iam-service/prod | jq '.propertySources[].name'
```

Expected output includes:

```text
"vault:iam-service"
"file:config-repo/iam-service/iam-service-prod.yaml"
```

If the Vault source is missing, check that:

1. Vault is unsealed.
2. `authgate-config-server` is running.
3. The config-server AppRole still exists and has the `config-server` policy.

## Config Server Policy

The config server policy is intentionally read-only:

```hcl
path "kv/data/*" {
  capabilities = ["read"]
}

path "kv/metadata/*" {
  capabilities = ["list"]
}
```

## Follow-Up Hardening

- Decide when to revoke the initial root token after admin access is established.
- Add a backup and restore procedure for the Vault data volume.
- Consider auto-unseal or HA storage if this becomes a higher-availability environment.
