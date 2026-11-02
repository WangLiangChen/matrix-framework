-- PostgreSQL reference schema. The sample runtime uses the in-memory adapter.
-- In production, map each aggregate to normalized tables/JSONB attribute payloads as described in the infrastructure README.
CREATE TABLE IF NOT EXISTS product_attribute (
    attribute_id uuid PRIMARY KEY, code varchar(120) NOT NULL UNIQUE, name varchar(200) NOT NULL,
    data_type varchar(30) NOT NULL, value_mode varchar(30) NOT NULL, cardinality varchar(20) NOT NULL,
    unit_code varchar(30), localized boolean NOT NULL DEFAULT false, status varchar(20) NOT NULL,
    version bigint NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS product_attribute_option (
    attribute_id uuid NOT NULL REFERENCES product_attribute(attribute_id), option_code varchar(100) NOT NULL,
    name varchar(200) NOT NULL, swatch varchar(40), active boolean NOT NULL DEFAULT true,
    PRIMARY KEY(attribute_id, option_code)
);
CREATE TABLE IF NOT EXISTS product_type (
    product_type_id uuid PRIMARY KEY, code varchar(120) NOT NULL UNIQUE, name varchar(200) NOT NULL,
    status varchar(20) NOT NULL, version bigint NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS product_type_version (
    product_type_id uuid NOT NULL REFERENCES product_type(product_type_id), version_no integer NOT NULL,
    published_at timestamptz NOT NULL, schema_snapshot jsonb NOT NULL,
    PRIMARY KEY(product_type_id, version_no)
);
CREATE TABLE IF NOT EXISTS brand (
    brand_id uuid PRIMARY KEY, code varchar(120) NOT NULL UNIQUE, name varchar(200) NOT NULL,
    status varchar(20) NOT NULL, version bigint NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS category (
    category_id uuid PRIMARY KEY, code varchar(120) NOT NULL UNIQUE, parent_category_id uuid REFERENCES category(category_id),
    name varchar(200) NOT NULL, slug varchar(200) NOT NULL UNIQUE, product_type_policy jsonb NOT NULL,
    status varchar(20) NOT NULL, version bigint NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS spu (
    spu_id uuid PRIMARY KEY, tenant_id uuid NOT NULL, spu_code varchar(120) NOT NULL,
    product_type_id uuid NOT NULL REFERENCES product_type(product_type_id), product_type_version integer NOT NULL,
    brand_id uuid REFERENCES brand(brand_id), name varchar(300) NOT NULL, subtitle varchar(500), description text,
    status varchar(30) NOT NULL, variant_configuration jsonb NOT NULL, attributes jsonb NOT NULL,
    media jsonb NOT NULL, version bigint NOT NULL DEFAULT 0, created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(tenant_id, spu_code),
    FOREIGN KEY(product_type_id, product_type_version) REFERENCES product_type_version(product_type_id, version_no)
);
CREATE TABLE IF NOT EXISTS spu_category (
    spu_id uuid NOT NULL REFERENCES spu(spu_id), category_id uuid NOT NULL REFERENCES category(category_id),
    is_primary boolean NOT NULL DEFAULT false, assigned_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY(spu_id, category_id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_spu_one_primary_category ON spu_category(spu_id) WHERE is_primary;
CREATE TABLE IF NOT EXISTS sku (
    sku_id uuid PRIMARY KEY, tenant_id uuid NOT NULL, spu_id uuid NOT NULL REFERENCES spu(spu_id), sku_code varchar(120) NOT NULL,
    variant_key varchar(1000) NOT NULL, attributes jsonb NOT NULL, barcodes jsonb NOT NULL,
    package_specification jsonb, media jsonb NOT NULL, status varchar(30) NOT NULL,
    version bigint NOT NULL DEFAULT 0, created_at timestamptz NOT NULL DEFAULT now(), updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(tenant_id, sku_code), UNIQUE(tenant_id, spu_id, variant_key)
);
CREATE TABLE IF NOT EXISTS outbox_event (
    event_id uuid PRIMARY KEY, aggregate_type varchar(60) NOT NULL, aggregate_id uuid NOT NULL,
    event_type varchar(160) NOT NULL, occurred_at timestamptz NOT NULL, payload jsonb NOT NULL,
    published_at timestamptz, attempts integer NOT NULL DEFAULT 0, next_attempt_at timestamptz
);
CREATE INDEX IF NOT EXISTS ix_outbox_unpublished ON outbox_event(next_attempt_at, occurred_at) WHERE published_at IS NULL;
