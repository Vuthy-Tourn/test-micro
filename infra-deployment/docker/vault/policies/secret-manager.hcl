path "kv/data/*" {
  capabilities = ["create", "update", "read", "delete"]
}

path "kv/metadata/*" {
  capabilities = ["list", "read", "delete"]
}