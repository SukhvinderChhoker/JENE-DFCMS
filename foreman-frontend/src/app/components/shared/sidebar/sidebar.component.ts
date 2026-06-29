import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  @Input() tabs: { label: string; icon: string; route?: string }[] = [];
  @Input() activeTab = '';
  @Input() onTabSelect: (tab: string) => void = () => {};
}
