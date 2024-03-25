package sh.miles.collect.registry

import sh.miles.collect.CollectPlugin
import sh.miles.collect.collector.template.CollectorTemplate
import sh.miles.pineapple.collection.registry.FrozenRegistry

object CollectorTemplateRegistry : FrozenRegistry<CollectorTemplate, String>({
    CollectPlugin.plugin.json.asArray(
        CollectPlugin.plugin, "collector-templates.json", Array<CollectorTemplate>::class.java
    ).associateBy { it.key }
})
