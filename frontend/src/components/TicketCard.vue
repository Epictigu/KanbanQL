<template>
    <div class="ticket-card" v-if="ticket" :key="ticket.id"
         draggable="true"
         @dragstart="startDrag($event, ticket)">
        <span class="double-click-checker" @click="selectTicket"/>
        <span class="ticket-name">{{ ticket.title }}</span>
        <PrioritySelector :current-priority="ticket.priority" @selectPriority="selectPriority"/>
        <div class="tag-list">
            <TagView v-for="tagId in ticket.tags" :tag-id="tagId.id" :key="tagId.id"/>
            <div class="tag-edit">
                <i class="fa fa-solid fa-tags tag-edit-icon" @click="toggleTagEditor"/>
                <div class="tag-selector-overlay" v-if="shouldShowTagList">
                    <div class="tag-selector-line"
                         v-for="tag of tags"
                         @click="selectTag(tag)"
                    >
                        <TagView :tag-id="tag.id"/>
                        <i class="fa-solid fa-check ml-auto" v-if="isTagSelected(tag)"/>
                    </div>
                    <hr class="hr mt-2 mb-2"/>
                    <div class="tag-selector-line" @click="openAddTagModal">
                        <i class="fa-solid fa-plus ml-auto mr-auto"/>
                    </div>
                </div>
            </div>
        </div>
        <BackgroundBlocker v-if="shouldShowTagList" @click="toggleTagEditor" :custom-z-index="14"/>
        <Modal ref="addTagModal" save-button-text="Hinzufügen" @modalSave="addTag">
            <template v-slot:modal-title>
                Neuen Tag erstellen
            </template>
            <template v-slot:modal-body>
                <div class="input-group d-flex flex-column">
                    <label for="newTagName">Name des Tags:</label>
                    <input id="newTagName" v-model="newTagName" class="form-control w-100" type="text"/>
                </div>
            </template>
        </Modal>
    </div>
</template>

<script lang="ts">
import type {PropType} from "vue";
import {defineComponent} from "vue";
import type {Ticket} from "@/model/ticket";
import PrioritySelector from "@/components/PrioritySelector.vue";
import type {Priority} from "@/enum/priority";
import TagView from "@/components/TagView.vue";
import {useTagStore} from "@/stores/tagStore";
import type {Tag} from "@/model/tag";
import Modal from "@/components/utils/Modal.vue";
import BackgroundBlocker from "@/components/utils/BackgroundBlocker.vue";
import type {TagId} from "@/model/tagId";
import TicketService from "@/services/ticketService";
import TagService from "@/services/tagService";

export default defineComponent({
    name: "TicketCard",
    emits: ["TicketSelect"],
    components: {
        BackgroundBlocker,
        Modal,
        TagView,
        PrioritySelector
    },
    props: {
        ticket: {
            type: Object as PropType<Ticket>,
            required: true
        }
    },
    data() {
        return {
            shouldShowTagList: false,
            newTagName: "",
            doubleClickTimeout: null as number | null
        }
    },
    computed: {
        tags() {
            return this.tagStore.tags;
        }
    },
    setup() {
        const tagStore = useTagStore();

        return {tagStore};
    },
    methods: {
        selectPriority(priority: Priority) {
            TicketService.updatePriority(this.ticket.id, priority);
        },
        toggleTagEditor() {
            this.shouldShowTagList = !this.shouldShowTagList;
        },
        selectTag(tag: Tag) {
            let index = this.ticket.tags.findIndex((tagId) => tag.id == tagId.id);
            if (index >= 0) {
                this.ticket.tags.splice(index, 1);
            } else {
                this.ticket.tags.push({id: tag.id} as TagId);
            }
            TicketService.updateTags(this.ticket);
        },
        isTagSelected(tag: Tag) {
            let index = this.ticket.tags.findIndex((tagId) => tag.id == tagId.id);
            return index >= 0;
        },
        startDrag(event: DragEvent, ticket: Ticket) {
            if (!event.dataTransfer) {
                return;
            }
            if (this.shouldShowTagList || (this.$refs.addTagModal as typeof Modal).showModal) {
                event.preventDefault();
                return;
            }

            event.dataTransfer.dropEffect = "move";
            event.dataTransfer.effectAllowed = "move";
            event.dataTransfer.setData("ticketID", ticket.id);
        },
        openAddTagModal() {
            (this.$refs.addTagModal as typeof Modal).displayModal();
        },
        addTag() {
            if (this.newTagName === "") {
                return;
            }
            TagService.createNewTag(this.newTagName);
            this.newTagName = "";
        },
        selectTicket() {
            if (!this.doubleClickTimeout) {
                this.doubleClickTimeout = setTimeout(() => {
                    this.doubleClickTimeout = null;
                }, 300);
            } else {
                clearTimeout(this.doubleClickTimeout);
                this.doubleClickTimeout = null;

                this.$emit("TicketSelect", this.ticket);
            }
        }
    }
});
</script>

<style scoped>
.ticket-card {
    position: relative;
    flex-direction: column;
    display: flex;
    border: 1px solid var(--navigation-border-color);
    border-radius: 4px;
    width: 300px;
    margin: 10px 0;
    padding: 10px;
    cursor: pointer;
    box-shadow: var(--card-box-shadow-color) 1px 1px 2px;
    user-select: none;

    .double-click-checker {
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        width: 100%;
    }

    .tag-list {
        display: flex;

        .tag-view-container {
            z-index: 15;
        }

        .tag-edit-icon {
            margin-top: auto;
            margin-bottom: auto;
            padding: 5px;
            color: var(--card-tags-color);
            z-index: 15;

            &:hover {
                padding: 4px;
                border: 1px solid var(--card-tags-color);
                border-radius: 50%;
            }
        }

        .tag-edit {
            position: relative;

            .tag-selector-overlay {
                position: absolute;
                left: 1em;
                background: white;
                border: 1px solid var(--navigation-border-color);
                border-radius: 4px;
                padding: 10px;
                z-index: 100;

                .tag-selector-line {
                    display: flex;
                    width: 110px;
                    padding: 5px;
                    border-radius: 4px;

                    &:hover {
                        background: var(--basic-hover-color);
                    }

                    .fa-solid {
                        margin-top: auto;
                        margin-bottom: auto;
                        height: 100%;
                    }
                }
            }
        }
    }
}
</style>