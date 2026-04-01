local space = box.schema.space.create('kv', {
    if_not_exists = true,
    engine = 'memtx'
})

space:format({
    {name = 'key', type = 'string'},
    {name = 'value', type = 'varbinary', is_nullable = true}
})

space:create_index('primary', {
    type = 'tree',
    parts = {'key'},
    if_not_exists = true
})

print('KV space initialized')